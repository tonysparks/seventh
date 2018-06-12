package seventh.ai.basic.memory;

import seventh.game.events.SoundEmittedEvent;
import seventh.shared.SoundType;

public class SoundExpireStrategy implements ExpireStrategy{
    protected SoundEmittedEvent sound;
    protected boolean isValid;
    @Override
    public void expire() {
        this.sound.setId(-1);
        this.sound.setSoundType(SoundType.MUTE);
        this.isValid = false;		
    }
}