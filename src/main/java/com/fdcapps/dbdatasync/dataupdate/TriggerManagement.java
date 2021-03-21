package com.fdcapps.dbdatasync.dataupdate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class TriggerManagement {

    private static final Logger log = Logger.getLogger(TriggerManagement.class.getName());

    private TriggerManagement() {
    }

    public static void disableTriggers(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("ALTER TABLE tuga_pantallas DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_conexiones DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_coleccionesdatos DISABLE ALL TRIGGERS");

            st.execute("ALTER TABLE tuga_importaexcelcolumnas DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_conexiones DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_importaexcel DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_roles DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_importaexcelroles DISABLE ALL TRIGGERS");

            st.execute("ALTER TABLE tuga_campos DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_botonespantallas DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_eventos DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_condiciones DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_accionescondicion DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_parametrosaccion DISABLE ALL TRIGGERS");

            st.execute("ALTER TABLE tuga_reportes DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_reportesquery DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_treemenu DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_rolesaplicaciones DISABLE ALL TRIGGERS");

            st.execute("ALTER TABLE tuga_pdfencabezado DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_pdfinformes DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_pdfpartes DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_pdfpie DISABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_pdfpivot DISABLE ALL TRIGGERS");

            st.execute("ALTER TABLE tuga_ajaxsql DISABLE ALL TRIGGERS");
        } catch (SQLException e) {
            log.error("Error on disableTriggers " + e);
            throw e;
        }
    }

    public static void enableTriggers(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("ALTER TABLE tuga_pantallas ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_conexiones ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_coleccionesdatos ENABLE ALL TRIGGERS");

            st.execute("ALTER TABLE tuga_importaexcelcolumnas ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_conexiones ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_importaexcel ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_roles ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_importaexcelroles ENABLE ALL TRIGGERS");

            st.execute("ALTER TABLE tuga_campos ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_botonespantallas ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_eventos ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_condiciones ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_accionescondicion ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_parametrosaccion ENABLE ALL TRIGGERS");

            st.execute("ALTER TABLE tuga_reportes ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_reportesquery ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_treemenu ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_rolesaplicaciones ENABLE ALL TRIGGERS");

            st.execute("ALTER TABLE tuga_pdfencabezado ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_pdfinformes ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_pdfpartes ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_pdfpie ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_pdfpivot ENABLE ALL TRIGGERS");
            st.execute("ALTER TABLE tuga_ajaxsql ENABLE ALL TRIGGERS");
        } catch (SQLException e) {
            log.error("Error on disableTriggers " + e);
            throw e;
        }
    }
}
