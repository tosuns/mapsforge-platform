package de.fub.agg2graph.pt;

import gnu.trove.TIntProcedure;

/**
 * @author Sebastian Müller
 *
 */
public class PTIntProcedure implements TIntProcedure {
		private Integer lastId = null; 

		public boolean execute(int Id) {
			lastId = Id;
			return true;
		}

		public Integer getId() {
			return lastId;
		}
	}

