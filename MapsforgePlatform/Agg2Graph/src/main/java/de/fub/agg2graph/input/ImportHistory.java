/*******************************************************************************
 * Copyright (c) 2012 Johannes Mitlmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Affero Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/agpl-3.0.html
 * 
 * Contributors:
 *     Johannes Mitlmeier - initial API and implementation
 ******************************************************************************/
package de.fub.agg2graph.input;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ImportHistory {
	private Connection conn;

	public ImportHistory(File folder) {
		// create db connection
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:"
					+ folder.getAbsolutePath() + "/history.db");
			Statement stat = conn.createStatement();
			stat.executeUpdate("create table if not exists imported_files (name);");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean needsImport(File fileToImport) {
		if (conn == null) {
			return true;
		}
		Statement stat;
		try {
			stat = conn.createStatement();
			ResultSet rs = stat
					.executeQuery("select name from imported_files where name='"
							+ fileToImport.getName() + "';");
			while (rs.next()) {
				System.out.println(rs.getString(1));
				return false;
			}
			rs.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
	}

	public boolean wasImported(File importedFile) {
		try {
			PreparedStatement prep = conn
					.prepareStatement("insert into imported_files values (?);");
			prep.setString(1, importedFile.getName());
			return prep.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
}
