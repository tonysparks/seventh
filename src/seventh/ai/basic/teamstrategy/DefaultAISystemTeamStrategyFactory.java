package seventh.ai.basic.teamstrategy;

import seventh.ai.AISystem;
import seventh.ai.basic.DefaultAISystem;
import seventh.game.type.GameType;
import seventh.game.type.cmd.CommanderGameType;

public class DefaultAISystemTeamStrategyFactory extends TeamStrategyFactory {

    @Override
    protected TeamStrategy createCTFAlliedAIStrategy(AISystem aiSystem, GameType gameType) {
        return new CaptureTheFlagTeamStrategy((DefaultAISystem)aiSystem, gameType.getAlliedTeam());
    }

    @Override
    protected TeamStrategy createCTFAxisAIStrategy(AISystem aiSystem, GameType gameType) {
        return new CaptureTheFlagTeamStrategy((DefaultAISystem)aiSystem, gameType.getAxisTeam());
    }

    @Override
    protected TeamStrategy createOBJAlliedAIStrategy(AISystem aiSystem, GameType gameType) {
        return new ObjectiveTeamStrategy((DefaultAISystem)aiSystem, gameType.getAlliedTeam());
    }

    @Override
    protected TeamStrategy createOBJAxisAIStrategy(AISystem aiSystem, GameType gameType) {
        return new ObjectiveTeamStrategy((DefaultAISystem)aiSystem, gameType.getAxisTeam());
    }

    @Override
    protected TeamStrategy createCMDAlliedAIStrategy(AISystem aiSystem, GameType gameType) {
        return new CommanderTeamStrategy((CommanderGameType)gameType, (DefaultAISystem)aiSystem, gameType.getAlliedTeam());
    }

    @Override
    protected TeamStrategy createCMDAxisAIStrategy(AISystem aiSystem, GameType gameType) {
        return new CommanderTeamStrategy((CommanderGameType)gameType, (DefaultAISystem)aiSystem, gameType.getAxisTeam());
    }

    @Override
    protected TeamStrategy createTDMAlliedAIStrategy(AISystem aiSystem, GameType gameType) {
        return new TDMTeamStrategy((DefaultAISystem)aiSystem, gameType.getAlliedTeam());
    }

    @Override
    protected TeamStrategy createTDMAxisAIStrategy(AISystem aiSystem, GameType gameType) {
        return new TDMTeamStrategy((DefaultAISystem)aiSystem, gameType.getAxisTeam());
    }


}
