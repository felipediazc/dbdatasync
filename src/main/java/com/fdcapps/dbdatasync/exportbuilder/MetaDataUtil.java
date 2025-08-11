package com.fdcapps.dbdatasync.exportbuilder;

public class MetaDataUtil {

    private MetaDataUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Boolean isIntegerColumn(int type) {
        return type == java.sql.Types.SMALLINT || type == java.sql.Types.INTEGER;
    }

    public static Boolean isBigIntegerColumn(int type) {
        return type == java.sql.Types.BIGINT;
    }

    public static Boolean isDoubleColumn(int type) {
        return type == java.sql.Types.FLOAT || type == java.sql.Types.DOUBLE || type == java.sql.Types.REAL ||
                type == java.sql.Types.NUMERIC || type == java.sql.Types.DECIMAL;
    }

    public static Boolean isTimestampColumn(int type) {
        return type == java.sql.Types.TIMESTAMP;
    }

    public static Boolean isDateColumn(int type) {
        return type == java.sql.Types.DATE;
    }

    public static Boolean isBooleanColumn(int type) {
        return type == java.sql.Types.BOOLEAN || type == java.sql.Types.BIT;
    }

    public static DataToJsonValues getNullColumnType(int resultSetMetadataColumnType) {
        if (isIntegerColumn(resultSetMetadataColumnType)) {
            return DataToJsonValues.INT_NULL;
        } else if (isBigIntegerColumn(resultSetMetadataColumnType)) {
            return DataToJsonValues.BIGINT_NULL;
        } else if (isDoubleColumn(resultSetMetadataColumnType)) {
            return DataToJsonValues.DOUBLE_NULL;
        } else if (isTimestampColumn(resultSetMetadataColumnType)) {
            return DataToJsonValues.TIMESTAMP_NULL;
        } else if (isDateColumn(resultSetMetadataColumnType)) {
            return DataToJsonValues.DATE_NULL;
        } else if (isBooleanColumn(resultSetMetadataColumnType)) {
            return DataToJsonValues.BOOLEAN_NULL;
        } else {
            return DataToJsonValues.STRING_NULL;
        }
    }
}
