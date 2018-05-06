package seventh.client.gfx;

public abstract class WeaponClassDescription {

    protected static final String THOMPSON = "Thompson | 30/180 rnds";
    protected static final String M1_GARAND = "M1 Garand | 8/40 rnds";
    protected static final String SPRINGFIELD = "Springfield | 5/35 rnds";
    protected static final String MP40 = "MP40 | 32/160 rnds";
    protected static final String MP44 = "MP44 | 30/120 rnds";
    protected static final String KAR98 = "KAR-98 | 5/25 rnds";
    protected static final String RISKER = "MG-z | 21/42 rnds";
    protected static final String SHOTGUN = "Shotgun | 5/35 rnds";
    protected static final String ROCKET_LAUNCHER = "M1 | 5 rnds";
    protected static final String FLAME_THROWER = "Flame Thrower";
    
    protected static final String PISTOL = "Pistol | 9/27 rnds";
    
    public String getDescription() {
        String description = "";
        boolean nothingInMessage = true;
        
        String mainWeaponMessage = getMainWeaponMessage();
        String subWeaponMessage = getSubWeaponMessage();
        String grenadeMassege = getGrenadeMessage();
        
        if (mainWeaponMessage != null) {
            description += mainWeaponMessage;
            nothingInMessage = false;
        }
        
        if (subWeaponMessage != null) {
            if (!nothingInMessage)
                description += "\n";
            description += subWeaponMessage;
            nothingInMessage = false;
        }
        
        if (grenadeMassege != null) {
            if (!nothingInMessage)
                description += "\n";
            description += grenadeMassege;
            nothingInMessage = false;
        }
        
        return description;
    }
    
    protected abstract String getMainWeaponMessage();
    protected abstract String getSubWeaponMessage();
    protected abstract String getGrenadeMessage();
}

class ThompsonDescription extends WeaponClassDescription {

    @Override
    protected String getMainWeaponMessage() {
        return THOMPSON;
    }

    @Override
    protected String getSubWeaponMessage() {
        return PISTOL;
    }

    @Override
    protected String getGrenadeMessage() {
        return "2 Frag Grenades";
    }
    
}

class M1GarandDescription extends WeaponClassDescription {

    @Override
    protected String getMainWeaponMessage() {
        return M1_GARAND;
    }

    @Override
    protected String getSubWeaponMessage() {
        return PISTOL;
    }

    @Override
    protected String getGrenadeMessage() {
        return "2 Smoke Grenades";
    }
    
}

class SpringfieldDescription extends WeaponClassDescription {

    @Override
    protected String getMainWeaponMessage() {
        return SPRINGFIELD;
    }

    @Override
    protected String getSubWeaponMessage() {
        return PISTOL;
    }

    @Override
    protected String getGrenadeMessage() {
        return "1 Frag Grenades";
    }
    
}

class Mp40Description extends WeaponClassDescription {

    @Override
    protected String getMainWeaponMessage() {
        return MP40;
    }

    @Override
    protected String getSubWeaponMessage() {
        return PISTOL;
    }

    @Override
    protected String getGrenadeMessage() {
        return "2 Frag Grenades";
    }
    
}

class Mp44Description extends WeaponClassDescription {

    @Override
    protected String getMainWeaponMessage() {
        return MP44;
    }

    @Override
    protected String getSubWeaponMessage() {
        return PISTOL;
    }

    @Override
    protected String getGrenadeMessage() {
        return "2 Smoke Grenades";
    }
    
}

class Kar98Description extends WeaponClassDescription {

    @Override
    protected String getMainWeaponMessage() {
        return KAR98;
    }

    @Override
    protected String getSubWeaponMessage() {
        return PISTOL;
    }

    @Override
    protected String getGrenadeMessage() {
        return "1 Frag Grenade";
    }
    
}

class RiskerDescription extends WeaponClassDescription {

    @Override
    protected String getMainWeaponMessage() {
        return RISKER;
    }

    @Override
    protected String getSubWeaponMessage() {
        return PISTOL;
    }

    @Override
    protected String getGrenadeMessage() {
        return null;
    }
    
}

class ShotgunDescription extends WeaponClassDescription {

    @Override
    protected String getMainWeaponMessage() {
        return SHOTGUN;
    }

    @Override
    protected String getSubWeaponMessage() {
        return PISTOL;
    }

    @Override
    protected String getGrenadeMessage() {
        return null;
    }
    
}

class RocketLauncherDescription extends WeaponClassDescription {

    @Override
    protected String getMainWeaponMessage() {
        return ROCKET_LAUNCHER;
    }

    @Override
    protected String getSubWeaponMessage() {
        return PISTOL;
    }

    @Override
    protected String getGrenadeMessage() {
        return "5 Frag Grenades";
    }
    
}

class FlameThrowerDescription extends WeaponClassDescription {

    @Override
    protected String getMainWeaponMessage() {
        return FLAME_THROWER;
    }

    @Override
    protected String getSubWeaponMessage() {
        return PISTOL;
    }

    @Override
    protected String getGrenadeMessage() {
        return "2 Frag Grenades";
    }
    
}