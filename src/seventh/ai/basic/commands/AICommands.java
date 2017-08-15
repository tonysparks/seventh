/**
 * 
 */
package seventh.ai.basic.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seventh.ai.AICommand;
import seventh.ai.basic.Brain;
import seventh.ai.basic.DefaultAISystem;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.SequencedAction;
import seventh.ai.basic.actions.atom.CoverEntityAction;
import seventh.ai.basic.actions.atom.GuardAction;
import seventh.ai.basic.actions.atom.MoveToAction;
import seventh.game.GameInfo;
import seventh.game.PlayerInfo;
import seventh.game.entities.BombTarget;
import seventh.math.Vector2f;
import seventh.shared.Cons;

/**
 * Converts the {@link AICommand} to an {@link Action} that will be
 * delegated to a bot.
 * 
 * @author Tony
 *
 */
public class AICommands {
    interface Command {
        Action parse(Brain brain, String ... args);
    }
    
    private Actions goals;
    private GameInfo game;
    
    
    private Map<String, Command> aiCommands;
    
    /**
     * @param aiSystem
     */
    public AICommands(DefaultAISystem aiSystem) {
        this.goals = aiSystem.getGoals();
        this.game = aiSystem.getGame();
        this.aiCommands = new HashMap<String, Command>();
        this.aiCommands.put("plantBomb", new Command() {

            @Override
            public Action parse(Brain brain, String... args) {
                return goals.plantBomb();
            }
            
        });
        
        this.aiCommands.put("defuseBomb", new Command() {

            @Override
            public Action parse(Brain brain, String... args) {
                return goals.defuseBomb();
            }
            
        });
        
        this.aiCommands.put("defendBomb", new Command() {
            
            @Override
            public Action parse(Brain brain, String... args) {
                List<BombTarget> plantedBombs = brain.getWorld().getBombTargetsWithActiveBombs();
                if(!plantedBombs.isEmpty()) {
                    BombTarget target = (BombTarget)brain.getEntityOwner().getClosest(plantedBombs);
                    return goals.defendPlantedBomb(target);
                }
                
                return null;
            }
        });
        
        this.aiCommands.put("followMe", new Command() {

            @Override
            public Action parse(Brain brain, String... args) {
                if(args.length > 0) {
                    String pid = args[0];                    
                    PlayerInfo player = game.getPlayerById(Integer.parseInt(pid));
                    if(player.isAlive()) {
                        return new CoverEntityAction(player.getEntity());
                    }
                    
                }
                return null;
            }
            
        });
        
        this.aiCommands.put("defendLeader", new Command() {

            @Override
            public Action parse(Brain brain, String... args) {
                if(args.length > 0) {
                    String pid = args[0];                    
                    int playerId = Integer.parseInt(pid);
                    PlayerInfo player = game.getPlayerById(playerId);
                    if(player.isAlive()) {                        
                        return goals.defendLeader(player);
                    }
                    
                }
                return null;
            }
            
        });
        
        this.aiCommands.put("takeCover", new Command() {
            
            @Override
            public Action parse(Brain brain, String... args) {
                Vector2f attackDir = new Vector2f();
                if(args.length > 1) {
                    int x = Integer.parseInt(args[0]);
                    int y = Integer.parseInt(args[1]);
                    attackDir.set(x, y);
                }
                
                Action action = goals.takeCover(attackDir);
                return action;
            }
        });
        
        this.aiCommands.put("moveTo", new Command() {
            
            @Override
            public Action parse(Brain brain, String... args) {
                Vector2f dest = new Vector2f();
                if(args.length > 1) {
                    float x = Float.parseFloat(args[0]);
                    float y = Float.parseFloat(args[1]);
                    dest.set(x, y);
                }
                
                SequencedAction action = new SequencedAction("MoveToAndGuard");
                action.addLastAction(new MoveToAction(dest));
                action.addLastAction(new GuardAction());
                return action;
            }
        });
        this.aiCommands.put("surpressFire", new Command() {
            
            @Override
            public Action parse(Brain brain, String... args) {
                Vector2f dest = new Vector2f();
                if(args.length > 1) {
                    float x = Float.parseFloat(args[0]);
                    float y = Float.parseFloat(args[1]);
                    dest.set(x, y);
                }
                
                
                return goals.surpressFire(dest);
            }
        });        
        
        this.aiCommands.put("action", new Command() {
            
            @Override
            public Action parse(Brain brain, String... args) {
                if(args.length > 0) {
                    Action action = goals.getScriptedAction(args[0]);
                    return action;
                }
                return null;
            }
        });
    }
    
    /**
     * Compiles the {@link AICommand} into a {@link Action}
     * @param cmd
     * @return the {@link Action} is parsed successfully, otherwise false
     */
    public Action compile(Brain brain, AICommand cmd) {
        Action result = null;
        String message = cmd.getMessage();
        if(message != null && !"".equals(message)) {
            String[] msgs = message.split(",");
            try {
                Command command = this.aiCommands.get(msgs[0]);
                if(command != null) {
                    String[] args = new String[msgs.length -1];
                    System.arraycopy(msgs, 1, args, 0, args.length);
                    result = command.parse(brain, args);
                }
            }
            catch(Exception e) {
                Cons.println("Error parsing AICommand: " + e);
            }
        }
        
        return result;
    }
}
