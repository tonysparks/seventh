/*
 * see license.txt 
 */
package seventh.shared;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import seventh.server.ServerSeventhConfig;

/**
 * Simple interface to pipe out to debug console
 * 
 * @author Tony
 *
 */
public interface Debugable {

	/**
	 * {@link Debugable} hook
	 * 
	 * @author Tony
	 *
	 */
	public static interface DebugableListener {
		
		/**
		 * Initializes the debugger
		 * 
		 * @param config
		 * @throws Exception
		 */
		void init(ServerSeventhConfig config) throws Exception;
		
		/**
		 * Shuts down the debugger
		 */
		void shutdown();
		
		/**
		 * Listens for a {@link Debugable} 
		 * 
		 * @param debugable
		 */
		void onDebugable(Debugable debugable);
	}
	
	
	/**
	 * Chain of debug entry information
	 * 
	 * @author Tony
	 *
	 */
	public static class DebugInformation {
		private Map<String, Object> entries;
		
		/**
		 */
		public DebugInformation() {
			this.entries = new HashMap<>();
		}
		
		public DebugInformation add(String key, int value) {
			this.entries.put(key, value);
			return this;
		}
		public DebugInformation add(String key, float value) {
			this.entries.put(key, value);
			return this;
		}
		
		public DebugInformation add(String key, double value) {
			this.entries.put(key, value);
			return this;
		}
		
		public DebugInformation add(String key, long value) {
			this.entries.put(key, value);
			return this;
		}
				
		public DebugInformation add(String key, Object value) {
			this.entries.put(key, value);
			return this;
		}
		public DebugInformation add(String key, DebugInformation chain) {
			this.entries.put(key, chain);
			return this;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {		
			StringBuilder sb = new StringBuilder("{ ");
			boolean isFirst = true;
			for(Map.Entry<String, Object> e : this.entries.entrySet()) {
				if(!isFirst) {
					sb.append(",");
				}
				
				sb.append("\"").append(e.getKey()).append("\" : ");
				Object value = e.getValue();
				if(value == null) {
					sb.append("null");
				}
				else if(value instanceof Object[]) {
					sb.append(Arrays.toString((Object[])value));
				}
				else if(value instanceof String[][]) {
					Object[][] v = (Object[][])value;
					sb.append("[");
					boolean innerIsFirst = true;
					for(int i = 0; i < v.length; i++) {
						if(!innerIsFirst) {
							sb.append(",");
						}
						sb.append(Arrays.toString(v[i]));
						innerIsFirst = false;
					}
					sb.append("]");
				}
				else if(value instanceof String) {
					sb.append("\"").append(value).append("\"");	
				}
				else {
					sb.append(value);
				}
				
				isFirst = false;
			}
			sb.append("}");
			return sb.toString();
		} 
	}
	
	/**
	 * Retrieves the debug information of this object 
	 * 
	 * @return the debug information
	 */
	public DebugInformation getDebugInformation();
}
