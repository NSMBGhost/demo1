package com.example.demo.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class TradeDay {
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_trade_day.gmt_create
     *
     * @mbg.generated Thu Jan 14 19:27:48 CST 2021
     */
    private Date gmtCreate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_trade_day.deal_date
     *
     * @mbg.generated Thu Jan 14 19:27:48 CST 2021
     */
    private String dealDate;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_trade_day.id
     *
     * @return the value of t_trade_day.id
     *
     * @mbg.generated Thu Jan 14 19:27:48 CST 2021
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_trade_day.id
     *
     * @param id the value for t_trade_day.id
     *
     * @mbg.generated Thu Jan 14 19:27:48 CST 2021
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_trade_day.gmt_create
     *
     * @return the value of t_trade_day.gmt_create
     *
     * @mbg.generated Thu Jan 14 19:27:48 CST 2021
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_trade_day.gmt_create
     *
     * @param gmtCreate the value for t_trade_day.gmt_create
     *
     * @mbg.generated Thu Jan 14 19:27:48 CST 2021
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_trade_day.deal_date
     *
     * @return the value of t_trade_day.deal_date
     *
     * @mbg.generated Thu Jan 14 19:27:48 CST 2021
     */
    public String getDealDate() {
        return dealDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_trade_day.deal_date
     *
     * @param dealDate the value for t_trade_day.deal_date
     *
     * @mbg.generated Thu Jan 14 19:27:48 CST 2021
     */
    public void setDealDate(String dealDate) {
        this.dealDate = dealDate == null ? null : dealDate.trim();
    }

    /**
     * This enum was generated by MyBatis Generator.
     * This enum corresponds to the database table t_trade_day
     *
     * @mbg.generated Thu Jan 14 19:27:48 CST 2021
     */
    public enum Column {
        id("id", "id", "BIGINT", false),
        gmtCreate("gmt_create", "gmtCreate", "TIMESTAMP", false),
        dealDate("deal_date", "dealDate", "VARCHAR", false);

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        private static final String BEGINNING_DELIMITER = "`";

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        private static final String ENDING_DELIMITER = "`";

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        private final String column;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        private final boolean isColumnNameDelimited;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        private final String javaProperty;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        private final String jdbcType;

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        public String value() {
            return this.column;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        public String getValue() {
            return this.column;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        public String getJavaProperty() {
            return this.javaProperty;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        public String getJdbcType() {
            return this.jdbcType;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        Column(String column, String javaProperty, String jdbcType, boolean isColumnNameDelimited) {
            this.column = column;
            this.javaProperty = javaProperty;
            this.jdbcType = jdbcType;
            this.isColumnNameDelimited = isColumnNameDelimited;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        public String desc() {
            return this.getEscapedColumnName() + " DESC";
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        public String asc() {
            return this.getEscapedColumnName() + " ASC";
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        public static Column[] excludes(Column ... excludes) {
            ArrayList<Column> columns = new ArrayList<>(Arrays.asList(Column.values()));
            if (excludes != null && excludes.length > 0) {
                columns.removeAll(new ArrayList<>(Arrays.asList(excludes)));
            }
            return columns.toArray(new Column[]{});
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        public static Column[] all() {
            return Column.values();
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        public String getEscapedColumnName() {
            if (this.isColumnNameDelimited) {
                return new StringBuilder().append(BEGINNING_DELIMITER).append(this.column).append(ENDING_DELIMITER).toString();
            } else {
                return this.column;
            }
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table t_trade_day
         *
         * @mbg.generated Thu Jan 14 19:27:48 CST 2021
         */
        public String getAliasedEscapedColumnName() {
            return this.getEscapedColumnName();
        }
    }
}