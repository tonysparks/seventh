package seventh.ai.basic.teamstrategy;

import seventh.ai.AISystem;
import seventh.game.type.GameType;

public abstract class TeamStrategyFactory {
    
    public TeamStrategy createAlliedAIStrategy(AISystem aiSystem, GameType gameType) {
        TeamStrategy teamStrategy;
        switch(gameType.getType()) {
            case CTF:
                teamStrategy = createCTFAlliedAIStrategy(aiSystem, gameType);
                break;
            case OBJ:
                teamStrategy = createOBJAlliedAIStrategy(aiSystem, gameType);
                break;
            case CMD: 
                teamStrategy = createCMDAlliedAIStrategy(aiSystem, gameType);
                break;
            case TDM:
            default:
                teamStrategy = createTDMAlliedAIStrategy(aiSystem, gameType);
                break;
        }
        return teamStrategy;
    }
    
    public TeamStrategy createAxisAIStrategy(AISystem aiSystem, GameType gameType) {
        TeamStrategy teamStrategy;
        switch(gameType.getType()) {
            case CTF:
                teamStrategy = createCTFAxisAIStrategy(aiSystem, gameType);
                break;
            case OBJ:
                teamStrategy = createOBJAxisAIStrategy(aiSystem, gameType);
                break;
            case CMD: 
                teamStrategy = createCMDAxisAIStrategy(aiSystem, gameType);
                break;
            case TDM:
            default:
                teamStrategy = createTDMAxisAIStrategy(aiSystem, gameType);
                break;
        }
        return teamStrategy;
    }
    
    protected abstract TeamStrategy createCTFAlliedAIStrategy(AISystem aiSystem, GameType gameType);
    protected abstract TeamStrategy createCTFAxisAIStrategy(AISystem aiSystem, GameType gameType);
    
    protected abstract TeamStrategy createOBJAlliedAIStrategy(AISystem aiSystem, GameType gameType);
    protected abstract TeamStrategy createOBJAxisAIStrategy(AISystem aiSystem, GameType gameType);
    
    protected abstract TeamStrategy createCMDAlliedAIStrategy(AISystem aiSystem, GameType gameType);
    protected abstract TeamStrategy createCMDAxisAIStrategy(AISystem aiSystem, GameType gameType);
    
    protected abstract TeamStrategy createTDMAlliedAIStrategy(AISystem aiSystem, GameType gameType);
    protected abstract TeamStrategy createTDMAxisAIStrategy(AISystem aiSystem, GameType gameType);
    
}
