package cyclegame.rotation;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import common.DBSyncer;
import cyclegame.players.ProPlayer;

/**
 * Created by luke on 11/15/15.
 */
public class DefaultDBSyncer implements DBSyncer {
	DBCollection dbCollection;

	public DefaultDBSyncer(DBCollection dbCollection) {
		this.dbCollection = dbCollection;
	}

	public void sync(ProPlayer proPlayer) {
		proPlayer.gameStats.clear();

		DBObject doc = this.dbCollection.findOne(new BasicDBObject("uuid",
				proPlayer.player.getUniqueId().toString()));

		if (doc != null) {
			BasicDBList list = (BasicDBList) doc.get("kits");
			for (Object o : list) {
				proPlayer.gameStats.kits.add((String) o);
			}

			proPlayer.gameStats.kills = (int) doc.get("kills");
			proPlayer.gameStats.deaths = (int) doc.get("deaths");
			proPlayer.gameStats.matches = (int) doc.get("matches");
		} else {
			BasicDBObject insert = new BasicDBObject("uuid", proPlayer.player
					.getUniqueId().toString());

			List<String> kits = new ArrayList<>();
			insert.put("kits", kits);

			insert.put("kills", 0);
			insert.put("deaths", 0);
			insert.put("matches", 0);

			this.dbCollection.insert(insert);
		}
	}

	@Override
	public void preSync(ProPlayer proPlayer) {

	}

	@Override
	public DBCollection getDbCollection() {
		return dbCollection;
	}

	@Override
	public void unload() {

	}
}
