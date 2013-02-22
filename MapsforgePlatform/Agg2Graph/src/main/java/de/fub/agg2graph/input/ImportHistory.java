/*******************************************************************************
   Copyright 2013 Johannes Mitlmeier

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
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
