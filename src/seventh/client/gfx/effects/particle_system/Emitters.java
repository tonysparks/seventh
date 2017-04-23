/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.entities.ClientEntity;
import seventh.client.gfx.Art;
import seventh.client.gfx.TextureUtil;
import seventh.client.gfx.effects.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.client.gfx.effects.particle_system.Emitter.ParticleUpdater;
import seventh.map.Tile;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class Emitters {

    public static Emitter newFireEmitter(Vector2f pos) {
        Emitter emitter = new Emitter(pos, 10_000, 100_000).setDieInstantly(false);
        emitter.addParticleGenerator(new FireGenerator(1.99f, 0xEB502Fff));
        //emitter.addParticleGenerator(new FireGenerator(0.954f, 0xFFFF09ff));
        emitter.addParticleUpdater(new KillUpdater());
        emitter.addParticleUpdater(new FireUpdater(12.2f));
        emitter.addParticleRenderer(new FireParticleRenderer());
        return emitter;
    }
    
    public static Emitter newBloodEmitter(Vector2f pos, int maxParticles, int emitterTimeToLive, int maxSpread) {        
        Emitter emitter = new Emitter(pos, emitterTimeToLive, maxParticles)
                                .setName("BloodEmitter")
                                .setDieInstantly(false);
        BatchedParticleGenerator gen = new BatchedParticleGenerator(0, maxParticles)
           .addSingleParticleGenerator(new RandomPositionInRadiusSingleParticleGenerator(maxSpread))
           .addSingleParticleGenerator(new RandomRotationSingleParticleGenerator())
           .addSingleParticleGenerator(new RandomScaleSingleParticleGenerator(0.55f, 1.2f))
           .addSingleParticleGenerator(new RandomSpeedSingleParticleGenerator(5, 5))
           .addSingleParticleGenerator(new RandomTimeToLiveSingleParticleGenerator(emitterTimeToLive, emitterTimeToLive))
           .addSingleParticleGenerator(new RandomSpriteSingleParticleGenerator(Art.bloodImages))
        ;
        
        emitter.addParticleGenerator(gen);
        
        emitter.addParticleUpdater(new KillUpdater());
        emitter.addParticleUpdater(new MovementParticleUpdater(0, 2));
        emitter.addParticleUpdater(new AlphaDecayUpdater(0f, 0.9878f));
        emitter.addParticleRenderer(new SpriteParticleRenderer());
        
        return emitter;
    }
    
    public static Emitter newBulletImpactFleshEmitter(Vector2f pos, Vector2f targetVel) {
        // 5, 5200, 4000, 0, 60);
        // int maxParticles, int emitterTimeToLive, int particleTimeToLive, int timeToNextSpawn, int maxSpread) {
        
        Vector2f vel = targetVel.isZero() ? new Vector2f(-1.0f, -1.0f) : new Vector2f(-targetVel.x*1.0f, -targetVel.y*1.0f);
        
        Emitter emitter = new Emitter(pos, 200, 30)
                            .setName("BulletImpactFleshEmitter")
                            .setDieInstantly(false);
        BatchedParticleGenerator gen = new BatchedParticleGenerator(0, 30);
        gen.addSingleParticleGenerator(new SingleParticleGenerator() {
            
                @Override
                public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
                    particles.speed[index] = 125f;
                }
            })
           .addSingleParticleGenerator(new SetPositionSingleParticleGenerator()) 
           .addSingleParticleGenerator(new RandomColorSingleParticleGenerator(new Color(0x660000ff), new Color(0x330000ff)))
           .addSingleParticleGenerator(new RandomVelocitySingleParticleGenerator(vel, 120))
           .addSingleParticleGenerator(new RandomTimeToLiveSingleParticleGenerator(600, 950))           
        ;
        
        emitter.addParticleGenerator(gen);
        
        emitter.addParticleUpdater(new KillUpdater());
        emitter.addParticleUpdater(new RandomMovementParticleUpdater(45));
        emitter.addParticleUpdater(new AlphaDecayUpdater(0f, 0.82718f));
        emitter.addParticleRenderer(new CircleParticleRenderer());
        
        return emitter;
    }
    
    public static Emitter newGibEmitter(Vector2f pos, int maxParticles) {
        int emitterTimeToLive = 10_000;
        int maxSpread = 35;
        
        Emitter emitter = new Emitter(pos, emitterTimeToLive, maxParticles)
                                .setName("GibEmitter")
                                .setDieInstantly(false);
        BatchedParticleGenerator gen = new BatchedParticleGenerator(0, maxParticles)
           .addSingleParticleGenerator(new RandomPositionInRadiusSingleParticleGenerator(maxSpread))
           .addSingleParticleGenerator(new RandomRotationSingleParticleGenerator())
           .addSingleParticleGenerator(new RandomScaleSingleParticleGenerator(0.15f, 1.2f))
           .addSingleParticleGenerator(new RandomSpeedSingleParticleGenerator(0, 0))
           .addSingleParticleGenerator(new RandomTimeToLiveSingleParticleGenerator(emitterTimeToLive, emitterTimeToLive))
           .addSingleParticleGenerator(new RandomSpriteSingleParticleGenerator(Art.gibImages))
        ;
        
        emitter.addParticleGenerator(gen);
        
        emitter.addParticleUpdater(new KillUpdater());
        emitter.addParticleUpdater(new MovementParticleUpdater(0, 2));
        emitter.addParticleUpdater(new AlphaDecayUpdater(0f, 0.9898f));
        emitter.addParticleRenderer(new SpriteParticleRenderer());
        
        return emitter;
    }
    
    public static Emitter newRocketTrailEmitter(Vector2f pos, int emitterTimeToLive) {
        //int emitterTimeToLive = 10_000;
        int maxParticles = 1000;
        int maxSpread = 15;
        
        Emitter emitter = new Emitter(pos, emitterTimeToLive, maxParticles)
                                .setName("RocketTrailEmitter")
                                .setDieInstantly(false);
        BatchedParticleGenerator gen = new BatchedParticleGenerator(0, 7)
            .addSingleParticleGenerator(new RandomColorSingleParticleGenerator(new Color(0x474B48ff),new Color(0x434B43ff)))
            .addSingleParticleGenerator(new SingleParticleGenerator() {
                
                @Override
                public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
                    particles.color[index].a = 0.92f;
                }
            })
           .addSingleParticleGenerator(new RandomPositionInRadiusSingleParticleGenerator(maxSpread))
           .addSingleParticleGenerator(new RandomRotationSingleParticleGenerator())
           .addSingleParticleGenerator(new RandomScaleSingleParticleGenerator(0.25f, 1.2f))
           .addSingleParticleGenerator(new RandomSpeedSingleParticleGenerator(0, 0))
           .addSingleParticleGenerator(new RandomTimeToLiveSingleParticleGenerator(5500, 6000))
           .addSingleParticleGenerator(new RandomSpriteSingleParticleGenerator(Art.smokeImage)) 
        ;
        
        emitter.addParticleGenerator(gen);
        
        emitter.addParticleUpdater(new KillUpdater());
        emitter.addParticleUpdater(new KillIfAttachedIsDeadUpdater());        
        emitter.addParticleUpdater(new RandomMovementParticleUpdater(80));
        emitter.addParticleUpdater(new AlphaDecayUpdater(0f, 0.9498f));
        emitter.addParticleRenderer(new SpriteParticleRenderer());
        
        return emitter;
    }
    
    public static Emitter newSmokeEmitter(Vector2f pos, int emitterTimeToLive) {
        return newSmokeEmitter(pos, emitterTimeToLive, 35, 0.55f, 1.2f);        
    }
    
    public static Emitter newSmokeEmitter(Vector2f pos, int emitterTimeToLive, int colorStart, int colorEnd) {        
        return newSmokeEmitter(pos, emitterTimeToLive, colorStart, colorEnd, 35, 0.55f, 1.2f);
    }
    
    public static Emitter newSmokeEmitter(Vector2f pos, int emitterTimeToLive, int maxSpread, float minSize, float maxSize) {        
        return newSmokeEmitter(pos, emitterTimeToLive, 0x838B8Bff, 0x838B83ff, maxSpread, minSize, maxSize);
    }
    
    public static Emitter newSmokeEmitter(Vector2f pos, int emitterTimeToLive, int colorStart, int colorEnd, int maxSpread, float minSize, float maxSize) {
        int maxParticles = 1500;
                
        Emitter emitter = new Emitter(pos, emitterTimeToLive, maxParticles)
                                .setName("SmokeEmitter")
                                .setDieInstantly(false);
        BatchedParticleGenerator gen = new BatchedParticleGenerator(100, 4)
           .addSingleParticleGenerator(new RandomColorSingleParticleGenerator(new Color(colorStart), new Color(colorEnd)))
           .addSingleParticleGenerator(new SingleParticleGenerator() {
                
                @Override
                public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
                    particles.color[index].a = 0.32f;
                }
            })
           .addSingleParticleGenerator(new RandomPositionInRadiusSingleParticleGenerator(maxSpread))
           .addSingleParticleGenerator(new RandomRotationSingleParticleGenerator())
           .addSingleParticleGenerator(new RandomScaleSingleParticleGenerator(minSize, maxSize))
           .addSingleParticleGenerator(new RandomSpeedSingleParticleGenerator(1, 1))
           .addSingleParticleGenerator(new RandomVelocitySingleParticleGenerator(new Vector2f(1,0), 180))
           .addSingleParticleGenerator(new RandomTimeToLiveSingleParticleGenerator(3800, 4000))
           .addSingleParticleGenerator(new RandomSpriteSingleParticleGenerator(Art.smokeImage)) 
        ;
        
        emitter.addParticleGenerator(gen);
        
        emitter.addParticleUpdater(new KillUpdater());
        emitter.addParticleUpdater(new KillIfAttachedIsDeadUpdater());        
        emitter.addParticleUpdater(new RandomMovementParticleUpdater(20));
        //emitter.addParticleUpdater(new MovementParticleUpdater(0, 40f));
        emitter.addParticleUpdater(new AlphaDecayUpdater(0f, 0.9898f));
        emitter.addParticleRenderer(new SpriteParticleRenderer());
        
        return emitter;
    }
    
    public static Emitter newGunSmokeEmitter(final ClientEntity entity, int emitterTimeToLive) {
        int maxParticles = 1500;
        final int maxSpread = 5;
        final Vector2f tmp = new Vector2f(entity.getPos());
        final Vector2f vel = entity.getFacing();
                
        Emitter emitter = new Emitter(tmp, emitterTimeToLive, maxParticles)
                                .setName("GunSmokeEmitter")
                                .setDieInstantly(false);
        
        BatchedParticleGenerator gen = new BatchedParticleGenerator(0, 4)
           .addSingleParticleGenerator(new RandomColorSingleParticleGenerator(new Color(0xDCDCDCff),new Color(0xD3D3D3ff)))
           .addSingleParticleGenerator(new SingleParticleGenerator() {
                
                @Override
                public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
                    particles.color[index].a = 0.32f;
                    
                    if(maxSpread > 0) {
                        Random r = particles.emitter.getRandom();
                                                
                        Vector2f pos = particles.pos[index];
                        pos.set(1,0);
                        
                        Vector2f.Vector2fRotate(pos, Math.toRadians(r.nextInt(360)), pos);
                        Vector2f.Vector2fMA(entity.getPos(), entity.getFacing(), 25.0f, tmp);
                        Vector2f.Vector2fMA(tmp, pos, r.nextInt(maxSpread), pos);
                    }
                }
            })
           .addSingleParticleGenerator(new RandomRotationSingleParticleGenerator())
           .addSingleParticleGenerator(new RandomScaleSingleParticleGenerator(0.25f, 0.30f))
           .addSingleParticleGenerator(new RandomSpeedSingleParticleGenerator(1, 1))
           .addSingleParticleGenerator(new RandomVelocitySingleParticleGenerator(vel, 130))
           .addSingleParticleGenerator(new RandomTimeToLiveSingleParticleGenerator(3800, 4000))
           .addSingleParticleGenerator(new RandomSpriteSingleParticleGenerator(Art.smokeImage)) 
        ;
        
        emitter.addParticleGenerator(gen);
        
        emitter.addParticleUpdater(new KillUpdater());
        emitter.addParticleUpdater(new KillIfAttachedIsDeadUpdater());        
        emitter.addParticleUpdater(new RandomMovementParticleUpdater(20));
        //emitter.addParticleUpdater(new MovementParticleUpdater(0, 40f));
        emitter.addParticleUpdater(new AlphaDecayUpdater(0f, 0.9898f));
        emitter.addParticleRenderer(new SpriteParticleRenderer());
        
        return emitter;
    }
    
    public static Emitter newBulletTracerEmitter(Vector2f pos, int emitterTimeToLive) {
        int maxParticles = 68;
                
        final Emitter emitter = new Emitter(pos, emitterTimeToLive, maxParticles)
                                    .setName("BulletTracerEmitter")
                                    .setDieInstantly(false);
        BatchedParticleGenerator gen = new BatchedParticleGenerator(0, maxParticles)
           .addSingleParticleGenerator(new RandomColorSingleParticleGenerator(new Color(0xffbA00ff),new Color(0xffff00ff)))
           .addSingleParticleGenerator(new SingleParticleGenerator() {
                
                @Override
                public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {            
                    particles.color[index].a = 0.0f;
                }
            })                                 
           .addSingleParticleGenerator(new SetPositionSingleParticleGenerator()) 
        ;
        
        emitter.addParticleGenerator(gen);
        emitter.addParticleUpdater(new ParticleUpdater() {
            
            @Override
            public void update(TimeStep timeStep, ParticleData particles) {
                ClientEntity ent = emitter.attachedTo();
                if(ent!=null) {
                    Vector2f pos = ent.getCenterPos();
                    Vector2f vel = ent.getMovementDir();
                    for(int index = 0; index < particles.numberOfAliveParticles; index++) {
                        float offset = index;
                        Vector2f.Vector2fMS(pos, vel, offset, particles.pos[index]);
                        
                        float percentange = (float)index / particles.numberOfAliveParticles;
                        
                        particles.color[index].a = 0.9512f * (1f - percentange);                        
                    }                    
                }
                else {
                    for(int index = 0; index < particles.numberOfAliveParticles; index++) {
                        particles.color[index].a = 0f;
                    }
                }
            }
        });
        
        emitter.addParticleUpdater(new KillIfAttachedIsDeadUpdater());    
        emitter.addParticleRenderer(new RectParticleRenderer(2,2));
        
        return emitter;
    }
        
    public static Emitter newBulletImpactEmitter(Vector2f pos, Vector2f targetVel) {
        // 5, 5200, 4000, 0, 60);
        // int maxParticles, int emitterTimeToLive, int particleTimeToLive, int timeToNextSpawn, int maxSpread) {
        
        Vector2f vel = targetVel.isZero() ? new Vector2f(-1.0f, -1.0f) : new Vector2f(-targetVel.x*1.0f, -targetVel.y*1.0f);
        
        Emitter emitter = new Emitter(pos, 200, 30)
                            .setName("BulletImpactEmitter")
                            .setDieInstantly(false);
        BatchedParticleGenerator gen = new BatchedParticleGenerator(0, 30);
        gen.addSingleParticleGenerator(new SingleParticleGenerator() {
            
                @Override
                public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
                    particles.speed[index] = 125f;
                }
            })
           .addSingleParticleGenerator(new SetPositionSingleParticleGenerator()) 
           .addSingleParticleGenerator(new RandomColorSingleParticleGenerator(new Color(0x8B7355ff), new Color(0x8A7355ff)))
           .addSingleParticleGenerator(new RandomVelocitySingleParticleGenerator(vel, 60))
           .addSingleParticleGenerator(new RandomTimeToLiveSingleParticleGenerator(200, 250))           
        ;
        
        emitter.addParticleGenerator(gen);
        
        emitter.addParticleUpdater(new KillUpdater());
        emitter.addParticleUpdater(new RandomMovementParticleUpdater(85));
        emitter.addParticleUpdater(new AlphaDecayUpdater(0f, 0.82718f));
        emitter.addParticleRenderer(new CircleParticleRenderer());
        
        return emitter;
    }
    
    public static Emitter newTankTrackSplatterEmitter(Vector2f pos, Vector2f targetVel) {
        // 5, 5200, 4000, 0, 60);
        // int maxParticles, int emitterTimeToLive, int particleTimeToLive, int timeToNextSpawn, int maxSpread) {
        
        Vector2f vel = targetVel.isZero() ? new Vector2f(-1.0f, -1.0f) : new Vector2f(-targetVel.x*1.0f, -targetVel.y*1.0f);
        
        Emitter emitter = new Emitter(pos, 200, 30)
                            .setName("BulletImpactEmitter")
                            .setDieInstantly(false);
        BatchedParticleGenerator gen = new BatchedParticleGenerator(0, 30);
        gen.addSingleParticleGenerator(new SingleParticleGenerator() {
            
                @Override
                public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
                    particles.speed[index] = 125f;
                }
            })
           .addSingleParticleGenerator(new SetPositionSingleParticleGenerator()) 
           .addSingleParticleGenerator(new RandomColorSingleParticleGenerator(new Color(0x8B7355ff), new Color(0x8A7355ff)))
           .addSingleParticleGenerator(new RandomVelocitySingleParticleGenerator(vel, 30))
           .addSingleParticleGenerator(new RandomTimeToLiveSingleParticleGenerator(200, 250))           
        ;
        
        emitter.addParticleGenerator(gen);
        
        emitter.addParticleUpdater(new KillUpdater());
        emitter.addParticleUpdater(new RandomMovementParticleUpdater(85));
        emitter.addParticleUpdater(new AlphaDecayUpdater(0f, 0.72718f));
        emitter.addParticleRenderer(new CircleParticleRenderer());
        
        return emitter;
    }
    
    
    public static Emitter newWallCrumbleEmitter(Tile tile, Vector2f pos) {
        final int maxParticles = 44;
        final int timeToLive = 100_000;
        
        Emitter emitter = new Emitter(pos, timeToLive, maxParticles)
                .setPersistent(true)
                .setDieInstantly(false);
        
        Random rand = emitter.getRandom();
        // TODO: fix the flipping business
        // in tiles, this is driving me nuts!
        Sprite image = new Sprite(tile.getImage());
        image.flip(false, true);
        
        TextureRegion[] images = new TextureRegion[44];
        int i = 0;
        for(; i < images.length-15; i++) {
            int x = rand.nextInt(image.getRegionWidth());
            int y = rand.nextInt(image.getRegionHeight());
            int width = rand.nextInt(image.getRegionWidth()/2);
            int height = rand.nextInt(image.getRegionHeight()/2);
            
            if(x+width > image.getRegionWidth()) {                
                width = image.getRegionWidth() - x;
            }
            
            if(y+height > image.getRegionHeight()) {
                height = image.getRegionHeight() - y;
            }
                        
            Sprite sprite = new Sprite(image);
            TextureUtil.setFlips(sprite, tile.isFlippedHorizontal(), tile.isFlippedVertical(), tile.isFlippedDiagnally());
            sprite.setRegion(sprite, x, y, width, height);
            
            images[i] = sprite;                    
        }
        
        Sprite sprite = new Sprite(Art.BLACK_IMAGE, 0, 0, 2, 4);
        for(;i<images.length;i++) {
            images[i] = sprite;
        }
        
        
        BatchedParticleGenerator gen = new BatchedParticleGenerator(0, maxParticles)
           .addSingleParticleGenerator(new RandomPositionInRadiusSingleParticleGenerator(10))
           .addSingleParticleGenerator(new RandomRotationSingleParticleGenerator())
           //.addSingleParticleGenerator(new RandomScaleSingleParticleGenerator(0.15f, 1.9f))
           .addSingleParticleGenerator(new RandomSpeedSingleParticleGenerator(250f, 250f))
           .addSingleParticleGenerator(new RandomVelocitySingleParticleGenerator(new Vector2f(1,0), 180))
           .addSingleParticleGenerator(new RandomTimeToLiveSingleParticleGenerator(timeToLive, timeToLive))
           .addSingleParticleGenerator(new RandomSpriteSingleParticleGenerator(images))
        ;
        
        emitter.addParticleGenerator(gen);
        
        //emitter.addParticleUpdater(new KillUpdater());
        emitter.addParticleUpdater(new MovementParticleUpdater(0, 40f));
        //emitter.addParticleUpdater(new AlphaDecayUpdater(0f, 0.9878f));
        emitter.addParticleRenderer(new SpriteParticleRenderer());
        
        return emitter;
    }
    
    public static Emitter newSpawnEmitter(Vector2f pos, int emitterTimeToLive) {
        int maxParticles = 40;
        int maxSpread = 45;
        
        Emitter emitter = new Emitter(pos, emitterTimeToLive, maxParticles)
                                .setName("SpawnEmitter")
                                .setDieInstantly(false);
        BatchedParticleGenerator gen = new BatchedParticleGenerator(100, 2)
           .addSingleParticleGenerator(new RandomColorSingleParticleGenerator(new Color(0xffa701ff),new Color(0xeeb803ff),new Color(0xffb805ff)))
           .addSingleParticleGenerator(new SingleParticleGenerator() {
                
                @Override
                public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
                    particles.color[index].a = 0.82f;
                }
            })
           .addSingleParticleGenerator(new RandomPositionInRadiusSingleParticleGenerator(maxSpread))           
           .addSingleParticleGenerator(new RandomScaleSingleParticleGenerator(0.15f, 0.55f))
           .addSingleParticleGenerator(new RandomSpeedSingleParticleGenerator(1, 1))
           .addSingleParticleGenerator(new RandomVelocitySingleParticleGenerator(new Vector2f(1,0), 180))
           .addSingleParticleGenerator(new RandomTimeToLiveSingleParticleGenerator(1800, 2300))
           .addSingleParticleGenerator(new RandomSpriteSingleParticleGenerator(Art.smokeImage)) 
        ;
        
        emitter.addParticleGenerator(gen);
        
        emitter.addParticleUpdater(new KillUpdater());
        emitter.addParticleUpdater(new KillIfAttachedIsDeadUpdater());        
        emitter.addParticleUpdater(new RandomMovementParticleUpdater(40));
        //emitter.addParticleUpdater(new MovementParticleUpdater(0, 40f));
        emitter.addParticleUpdater(new AlphaDecayUpdater(0f, 0.96898f));
        emitter.addParticleRenderer(new SpriteParticleRenderer());
        
        return emitter;
    }
}
