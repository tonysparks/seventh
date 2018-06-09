package seventh.ai.basic.memory;

import seventh.game.entities.Entity;

public class FeelExpireStrategy implements ExpireStrategy{

    protected Entity damager;            
    protected boolean isValid;
	@Override
	public void expire() {
		this.damager = null;                                                        
        this.isValid = false;			
	}
	

}