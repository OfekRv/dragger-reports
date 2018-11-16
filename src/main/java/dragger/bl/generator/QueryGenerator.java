package dragger.bl.generator;

import dragger.entities.Query;
import dragger.exceptions.DraggerException;

public interface QueryGenerator extends ConnectionFinder {
	public String generate(Query query) throws DraggerException;
}
