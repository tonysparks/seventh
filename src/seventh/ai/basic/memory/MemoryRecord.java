package seventh.ai.basic.memory;

import seventh.ai.basic.memory.ExpireStrategy;

public abstract class MemoryRecord {
    protected final long expireTime;
    protected boolean isValid;
    ExpireStrategy expireStrategy;
    public MemoryRecord(long expireTime)
    {
    	this.expireTime = expireTime;
    	this.setValid(false);
    }
	public long getExpireTime() {
		return expireTime;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

}