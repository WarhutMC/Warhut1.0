package cyclegame.rotation;

import common.kit.Kit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luke on 10/17/15.
 *
 */
public abstract class KitRepo {
    // Put all purchasable kits in the kits array
    public List<Kit> kits = new ArrayList<>();

    // The free kit should be defined here, and NOT included in the kits array
    public Kit defaultKit;

    public KitRepo() {}

    public Kit addKit(Kit kit) {
        this.kits.add(kit);
        return kit;
    }

    public void setDefaultKit(Kit kit) {
        this.defaultKit = kit;
    }

    public void enableKits() {
        for (Kit kit : this.kits) {
            kit.enable();
        }
    }

    public void unload() {
        for (Kit kit : this.kits) {
            kit.unload();
        }
    }
}
