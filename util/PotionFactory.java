package common.util;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionFactory {

	public static PotionEffect createPotion(PotionEffectType type, int duration, int amplifier){
		
		PotionEffect pe = new PotionEffect(type, duration, amplifier);
		
		return pe;
	}
	
	public static PotionEffect createPotion(PotionEffectType type, int duration, int amplifier, boolean showParticles){
		
		PotionEffect pe = new PotionEffect(type, duration, amplifier, true, showParticles);
		
		return pe;
		
	}
	
}
