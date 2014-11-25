/*
 * see license.txt 
 */
package seventh.shared;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;

import leola.vm.Leola;
import leola.vm.types.LeoNull;
import leola.vm.types.LeoObject;
import seventh.game.GameInfo;
import seventh.game.PlayerInfo;
import seventh.game.PlayerInfos;
import seventh.game.net.NetGameTypeInfo;
import seventh.game.net.NetTeam;
import seventh.game.net.NetTeamStat;
import seventh.server.GameServer;
import seventh.server.ServerContext;
import seventh.server.ServerSeventhConfig;

/**
 * API to the Master Server.  The master server is responsible for keeping track of game servers
 * so that client know which are available.
 * 
 * @author Tony
 *
 */
public class MasterServerClient {

	/**
	 * Encoding
	 */
	private static final String ENCODING = "UTF8";
	
	private String masterServerUrl;
			
	private String proxyUser;
	private String proxyPw;
	
	private Proxy proxy;
	
	/**
	 * 
	 */
	public MasterServerClient(MasterServerConfig config) {
		this.masterServerUrl = config.getUrl();
				
		this.proxy = config.getProxy();
		
		if(this.proxy != null && !this.proxy.equals(Proxy.NO_PROXY)) {			
			this.proxyUser = config.getProxyUser();
			this.proxyPw = config.getProxyPassword();
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
			
			Leola runtime = Scripting.newSandboxedRuntime();
			result = runtime.eval(sb.toString());			
		}
		
		return (result);
	}
	
	/**
	 * Sends a heart beat to the master server
	 * @param gameServer the game server
	 * @throws Exception
	 */
	public void sendHeartbeat(ServerContext serverContext) throws Exception {
		URL url = new URL(this.masterServerUrl);
		HttpURLConnection conn = connect(url);
		
		conn.setRequestMethod("POST");
		conn.setRequestProperty("User-Agent", "Seventh Server");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		conn.setRequestProperty("seventh_version", GameServer.VERSION);
		
		String urlParameters = populateGameServerPingParameters(serverContext);
		
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
		if(this.proxy != null && !this.proxy.equals(Proxy.NO_PROXY)) {						
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
	
	private String populateGameServerPingParameters(ServerContext serverContext) throws Exception {
		ServerSeventhConfig config = serverContext.getConfig();
						
		StringBuilder sb = new StringBuilder();		
		sb.append("server_name=").append(URLEncoder.encode(config.getServerName(), ENCODING));		
		sb.append("&game_type=").append(URLEncoder.encode(config.getGameType().name(), ENCODING));
		if(serverContext.hasGameSession()) {
			GameInfo game = serverContext.getGameSession().getGame();
			sb.append("&map=").append(URLEncoder.encode(serverContext.getMapCycle().getCurrentMap(), ENCODING));
			sb.append("&time=").append(game.getGameType().getRemainingTime());
			
			NetTeamStat[] stats = game.getGameType().getNetTeamStats();
			sb.append("&axis_score=").append(stats[0].score);
			sb.append("&allied_score=").append(stats[1].score);
					
			NetGameTypeInfo info = game.getGameType().getNetGameTypeInfo();
			if(info!= null && info.teams!=null && info.teams.length > 1) {
				PlayerInfos players = game.getPlayerInfos();
				
				sb.append("&axis=");
				
				NetTeam axis = info.teams[0];		
				appendTeam(players, axis, sb);
				
				sb.append("&allied=");
				
				NetTeam allied = info.teams[1];
				appendTeam(players, allied, sb);
			}
		
		}
		sb.append("&port=").append(serverContext.getPort());
		
		return sb.toString();
	}
	
	private void appendTeam(PlayerInfos players, NetTeam team, StringBuilder sb) throws Exception {
		if(team!=null) {
			for(int i = 0; i < team.playerIds.length; i++) {
				PlayerInfo player = players.getPlayerInfo(team.playerIds[i]);
				if(player!=null) {
					sb.append("\"");
					sb.append(URLEncoder.encode(player.getName().replace(",", "."), ENCODING));
					sb.append("\"");
					sb.append(URLEncoder.encode(",", ENCODING));
				}
			}
		}
	}

}
