/*
 * see license.txt 
 */
package seventh.shared;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLEncoder;

import leola.vm.Leola;
import leola.vm.types.LeoMap;
import leola.vm.types.LeoNull;
import leola.vm.types.LeoObject;
import seventh.game.GameInfo;
import seventh.game.PlayerInfo;
import seventh.game.PlayerInfos;
import seventh.game.net.NetGameTypeInfo;
import seventh.game.net.NetTeam;
import seventh.game.net.NetTeamStat;
import seventh.server.GameServer;

/**
 * @author Tony
 *
 */
public class MasterServerApi {

	private String masterServerUrl;
	
	private String proxy;
	private int proxyPort;
	private String proxyUser;
	private String proxyPw;
	
	/**
	 * 
	 */
	public MasterServerApi(Config config) {
		this.masterServerUrl = config.getString("master_server", "url");
				
		LeoObject proxyObj = config.get("master_server", "proxy_settings");
		if(LeoObject.isTrue(proxyObj)) {
			LeoMap map = proxyObj.as();
			this.proxy = map.getString("address");
			this.proxyPort = map.getInt("port");
			this.proxyUser = map.getString("user");
			this.proxyPw = map.getString("password");
		}
	}
	
		
	/**
	 * Retrieves the Internet server listings.
	 * 
	 * @param gameType
	 * @throws Exception
	 */
	public LeoObject getServerListings(String gameType) throws Exception {
		LeoObject result = LeoNull.LEONULL;
		
		String requestUrl = (gameType!=null) ? this.masterServerUrl + "?game_type=" + gameType : this.masterServerUrl;
		URL url = new URL(requestUrl);
		HttpURLConnection conn = connect(url);
		
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent", "Seventh Client");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		conn.setRequestProperty("seventh_version", GameServer.VERSION);
		
		int responseCode = conn.getResponseCode();
		if(responseCode != 200) {
			Cons.println("Error response from master server: " + responseCode);
		}
		else {
			BufferedReader istream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			StringBuilder sb=new StringBuilder("return ");
			while( (line=istream.readLine()) != null) {
				sb.append(line.replace(":", "->")).append("\n");
			}
			sb.append(" ;");
			Leola leola = new Leola();
			result = leola.eval(sb.toString());			
		}
		
		return (result);
	}
	
	/**
	 * Sends a heart beat to the master server
	 * @param gameServer the game server
	 * @throws Exception
	 */
	public void sendHeartbeat(GameServer gameServer) throws Exception {
		URL url = new URL(this.masterServerUrl);
		HttpURLConnection conn = connect(url);
		
		conn.setRequestMethod("POST");
		conn.setRequestProperty("User-Agent", "Seventh Server");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		conn.setRequestProperty("seventh_version", GameServer.VERSION);
		
		String urlParameters = populateGameServerPingParameters(gameServer);
		
		// Send post request
		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		//Cons.println("Sending heartbeat request to: " + url);
		int responseCode = conn.getResponseCode();
		if(responseCode != 200) {
			Cons.println("Error response from master server: " + responseCode);
		}
	}
	
	
	/**
	 * Connects to the master server
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private HttpURLConnection connect(URL url) throws Exception {
		if(this.proxy != null) {
			Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(this.proxy, this.proxyPort));			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
			if(this.proxyUser!=null) {
				String uname_pwd = proxyUser + ":" + proxyPw;
				String authString = "Basic " + Base64.encodeBytes(uname_pwd.getBytes());
				connection.setRequestProperty("Proxy-Authorization", authString);
			}
			
			return connection;
		}
		return (HttpURLConnection) url.openConnection();
	}
	
	private String populateGameServerPingParameters(GameServer gameServer) throws Exception {
		GameInfo game = gameServer.getProtocolListener().getGame();
		
		StringBuilder sb = new StringBuilder();		
		sb.append("server_name=").append(URLEncoder.encode(gameServer.getServerName(), "UTF8"));		
		sb.append("&game_type=").append(URLEncoder.encode(gameServer.getGameType().name(), "UTF8"));
		if(game != null) {
			sb.append("&map=").append(URLEncoder.encode(gameServer.getMapCycle().getCurrentMap(), "UTF8"));
			sb.append("&time=").append(game.getGameType().getRemainingTime());
			
			NetTeamStat[] stats = game.getGameType().getNetTeamStats();
			sb.append("&axis_score=").append(stats[0].score);
			sb.append("&allied_score=").append(stats[1].score);
					
			NetGameTypeInfo info = game.getGameType().getNetGameTypeInfo();
			if(info!= null && info.teams!=null && info.teams.length > 1) {
				PlayerInfos players = game.getPlayerInfos();
				
				sb.append("&axis=");
				
				NetTeam axis = info.teams[0];		
				if(axis!=null) {
					for(int i = 0; i < axis.playerIds.length; i++) {
						PlayerInfo player = players.getPlayerInfo(axis.playerIds[i]);
						if(player!=null) {
							sb.append("\"");
							sb.append(URLEncoder.encode(player.getName().replace(",", "."), "UTF8"));
							sb.append("\"");
							sb.append(URLEncoder.encode(",", "UTF8"));
						}
					}
				}
				
				sb.append("&allied=");
				
				NetTeam allied = info.teams[1];	
				if(allied!=null) {
					for(int i = 0; i < allied.playerIds.length; i++) {
						PlayerInfo player = players.getPlayerInfo(allied.playerIds[i]);
						if(player!=null) {
							sb.append(URLEncoder.encode(player.getName().replace(",", "."), "UTF8"));
							sb.append(URLEncoder.encode(",", "UTF8"));
						}
					}
				}
			}
		
		}
		sb.append("&port=").append(gameServer.getPort());
		
		return sb.toString();
	}

}
