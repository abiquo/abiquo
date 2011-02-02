/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 * 
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA. */

package com.abiquo.server.core.common.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.classic.Session;
import org.hibernate.stat.Statistics;

public class HibernateSessionAllocationChecker {

	private boolean statisticsEnabledOnEntry;
	private long outstandingSessionsOnEntry;
	private int openMySqlConnectionsOnEntry;
	private long sessionsNotReleasedAfterCreation;
	private int mySqlConnectionsNoReleasedAfterCreation;
	private EntityManagerFactory factory;

	private int getMySqlConnectionCount(Connection connection)
			throws SQLException {
		assert connection != null;

		Statement query = connection.createStatement();
		try {
			ResultSet rs = query
					.executeQuery("show global status like 'Threads_connected'");
			try {
				boolean found = rs.next();
				assert found;

				String result = rs.getString(2);
				return Integer.parseInt(result);
			} finally {
				rs.close();
			}
		} finally {
			query.close();
		}
	}

	public HibernateSessionAllocationChecker(EntityManagerFactory factory) {
		assert factory != null;

		this.factory = factory;

		EntityManager em = factory.createEntityManager();
		try {
			Session session = (Session) em.getDelegate();
			Statistics statistics = session.getSessionFactory().getStatistics();
			this.statisticsEnabledOnEntry = statistics.isStatisticsEnabled();
			statistics.setStatisticsEnabled(true);
			this.outstandingSessionsOnEntry = statistics.getSessionOpenCount()
					- statistics.getSessionCloseCount();
			try {
				this.openMySqlConnectionsOnEntry = getMySqlConnectionCount(session
						.connection());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		} finally {
			em.close();
		}
	}

	private boolean resultObtained;

	private void ensureResultObtained() {
		if (!resultObtained) {
			EntityManager em = factory.createEntityManager();
			try {
				Session session = (Session) em.getDelegate();
				Statistics statistics = session.getSessionFactory()
						.getStatistics();

				assert statistics.isStatisticsEnabled() : "Somebody else disabled the statistics: DON'T do, or do not use this component";

				long outstandingSessionsOnExit = statistics
						.getSessionOpenCount()
						- statistics.getSessionCloseCount();
				this.sessionsNotReleasedAfterCreation = outstandingSessionsOnExit
						- this.outstandingSessionsOnEntry;

				assert sessionsNotReleasedAfterCreation >= 0 : "Somebody else played with the statistics (maybe cleared/disabled them?): DON'T do, or do not use this component";
				// System.err.println( "OUTSTANDING SESSIONS: " +
				// sessionsNotReleasedAfterCreation);

				try {
					this.mySqlConnectionsNoReleasedAfterCreation = getMySqlConnectionCount(session
							.connection())
							- this.openMySqlConnectionsOnEntry;
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}

				// Reset statistics to the state they were before we started to
				// be interested in open/closed session count
				statistics.setStatisticsEnabled(this.statisticsEnabledOnEntry);
			} finally {
				em.close();
			}
			resultObtained = true;
		}

	}

	public long getOutstandingMySqlConnections() {
		ensureResultObtained();
		return this.mySqlConnectionsNoReleasedAfterCreation;
	}

	public long getOutstandingSessions() {
		ensureResultObtained();
		return this.sessionsNotReleasedAfterCreation;
	}
}