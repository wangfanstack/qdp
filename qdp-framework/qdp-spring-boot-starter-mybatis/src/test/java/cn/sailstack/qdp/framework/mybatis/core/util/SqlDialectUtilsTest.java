package cn.sailstack.qdp.framework.mybatis.core.util;

import cn.sailstack.qdp.framework.common.util.spring.SpringUtils;
import cn.sailstack.qdp.framework.mybatis.core.util.SqlDialectUtils.Dialect;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SqlDialectUtils} 的单元测试
 *
 * @author 芋道源码
 */
public class SqlDialectUtilsTest {

    private static final String P = "${@cn.sailstack.qdp.framework.mybatis.core.util.SqlDialectUtils@";

    private ApplicationContext originContext;

    @BeforeEach
    public void setUp() {
        originContext = SpringUtils.getContext();
    }

    @AfterEach
    public void tearDown() {
        if (originContext != null) {
            new SpringUtils().setApplicationContext(originContext);
        }
    }

    // ========== SQL 片段 ==========

    @Test
    public void testFormatDate() {
        assertEquals("DATE_FORMAT(create_time, '%Y-%m-%d')", SqlDialectUtils.formatDate(Dialect.ORIGINAL, "create_time"));
        assertEquals("TO_CHAR(t.create_time, 'YYYY-MM-DD')", SqlDialectUtils.formatDate(Dialect.POSTGRESQL, "t.create_time"));
        assertEquals("TO_CHAR(create_time, 'YYYY-MM-DD')", SqlDialectUtils.formatDate(Dialect.ORACLE, "create_time"));
        assertEquals("CONVERT(varchar(10), create_time, 23)", SqlDialectUtils.formatDate(Dialect.SQLSERVER, "create_time"));
    }

    @Test
    public void testFormatYearMonth() {
        assertEquals("DATE_FORMAT(order_date, '%Y%m')", SqlDialectUtils.formatYearMonth(Dialect.ORIGINAL, "order_date"));
        assertEquals("TO_CHAR(order_date, 'YYYYMM')", SqlDialectUtils.formatYearMonth(Dialect.POSTGRESQL, "order_date"));
        assertEquals("TO_CHAR(order_date, 'YYYYMM')", SqlDialectUtils.formatYearMonth(Dialect.ORACLE, "order_date"));
        assertEquals("CONVERT(varchar(6), order_date, 112)", SqlDialectUtils.formatYearMonth(Dialect.SQLSERVER, "order_date"));
    }

    @Test
    public void testFormatYearDashMonth() {
        assertEquals("CONCAT(EXTRACT(YEAR FROM pay_time), '-', LPAD(CONCAT(EXTRACT(MONTH FROM pay_time)), 2, '0'))",
                SqlDialectUtils.formatYearDashMonth(Dialect.ORIGINAL, "pay_time"));
        assertEquals("TO_CHAR(pay_time, 'YYYY-MM')", SqlDialectUtils.formatYearDashMonth(Dialect.POSTGRESQL, "pay_time"));
        assertEquals("TO_CHAR(pay_time, 'YYYY-MM')", SqlDialectUtils.formatYearDashMonth(Dialect.ORACLE, "pay_time"));
        assertEquals("CONVERT(varchar(7), pay_time, 120)", SqlDialectUtils.formatYearDashMonth(Dialect.SQLSERVER, "pay_time"));
    }

    @Test
    public void testDate() {
        assertEquals("DATE(send_time)", SqlDialectUtils.date(Dialect.ORIGINAL, "send_time"));
        assertEquals("TO_CHAR(send_time, 'YYYY-MM-DD')", SqlDialectUtils.date(Dialect.POSTGRESQL, "send_time"));
        assertEquals("TO_CHAR(send_time, 'YYYY-MM-DD')", SqlDialectUtils.date(Dialect.ORACLE, "send_time"));
        assertEquals("CONVERT(varchar(10), send_time, 23)", SqlDialectUtils.date(Dialect.SQLSERVER, "send_time"));
    }

    @Test
    public void testCastDateText() {
        assertEquals("CAST(create_time AS DATE)", SqlDialectUtils.castDateText(Dialect.ORIGINAL, "create_time"));
        assertEquals("TO_CHAR(create_time, 'YYYY-MM-DD')", SqlDialectUtils.castDateText(Dialect.POSTGRESQL, "create_time"));
        assertEquals("TO_CHAR(create_time, 'YYYY-MM-DD')", SqlDialectUtils.castDateText(Dialect.ORACLE, "create_time"));
        assertEquals("CONVERT(varchar(10), create_time, 23)", SqlDialectUtils.castDateText(Dialect.SQLSERVER, "create_time"));
    }

    @Test
    public void testCastDate() {
        assertEquals("CAST(create_time AS DATE)", SqlDialectUtils.castDate(Dialect.ORIGINAL, "create_time"));
        assertEquals("CAST(create_time AS DATE)", SqlDialectUtils.castDate(Dialect.POSTGRESQL, "create_time"));
        assertEquals("TRUNC(create_time)", SqlDialectUtils.castDate(Dialect.ORACLE, "create_time"));
        assertEquals("CAST(create_time AS DATE)", SqlDialectUtils.castDate(Dialect.SQLSERVER, "create_time"));
    }

    @Test
    public void testMonth() {
        assertEquals("MONTH(order_date)", SqlDialectUtils.month(Dialect.ORIGINAL, "order_date"));
        assertEquals("CAST(EXTRACT(MONTH FROM order_date) AS INTEGER)", SqlDialectUtils.month(Dialect.POSTGRESQL, "order_date"));
        assertEquals("EXTRACT(MONTH FROM order_date)", SqlDialectUtils.month(Dialect.ORACLE, "order_date"));
        assertEquals("MONTH(order_date)", SqlDialectUtils.month(Dialect.SQLSERVER, "order_date"));
    }

    @Test
    public void testYear() {
        assertEquals("YEAR(return_time)", SqlDialectUtils.year(Dialect.ORIGINAL, "return_time"));
        assertEquals("CAST(EXTRACT(YEAR FROM return_time) AS INTEGER)", SqlDialectUtils.year(Dialect.POSTGRESQL, "return_time"));
        assertEquals("EXTRACT(YEAR FROM return_time)", SqlDialectUtils.year(Dialect.ORACLE, "return_time"));
        assertEquals("YEAR(return_time)", SqlDialectUtils.year(Dialect.SQLSERVER, "return_time"));
    }

    @Test
    public void testDayOfMonth() {
        assertEquals("DAYOFMONTH(birthday)", SqlDialectUtils.dayOfMonth(Dialect.ORIGINAL, "birthday"));
        assertEquals("CAST(EXTRACT(DAY FROM birthday) AS INTEGER)", SqlDialectUtils.dayOfMonth(Dialect.POSTGRESQL, "birthday"));
        assertEquals("EXTRACT(DAY FROM birthday)", SqlDialectUtils.dayOfMonth(Dialect.ORACLE, "birthday"));
        assertEquals("DAY(birthday)", SqlDialectUtils.dayOfMonth(Dialect.SQLSERVER, "birthday"));
    }

    @Test
    public void testCustomerDealCycle() {
        String original = "COALESCE(TRUNCATE(AVG(TIMESTAMPDIFF(DAY, customer.create_time, contract.order_date)), 1), 0)";
        assertEquals(original, SqlDialectUtils.customerDealCycle(Dialect.ORIGINAL, "customer.create_time", "contract.order_date"));
        // PostgreSQL：两个 TIMESTAMP 相减为 interval，EXTRACT(DAY) 即完整天数（向零截断、保留负号），TRUNC 截断保留 1 位
        assertEquals("COALESCE(TRUNC(AVG(CAST(EXTRACT(DAY FROM (CAST(contract.order_date AS TIMESTAMP) - "
                        + "CAST(customer.create_time AS TIMESTAMP))) AS NUMERIC)), 1), 0)",
                SqlDialectUtils.customerDealCycle(Dialect.POSTGRESQL, "customer.create_time", "contract.order_date"));
        // Oracle：升格为 TIMESTAMP(9)，DATE 与 TIMESTAMP(9) 均不丢失精度，INTERVAL 的 DAY 部分即完整天数
        assertEquals("COALESCE(TRUNC(AVG(EXTRACT(DAY FROM (CAST(contract.order_date AS TIMESTAMP(9)) - "
                        + "CAST(customer.create_time AS TIMESTAMP(9))))), 1), 0)",
                SqlDialectUtils.customerDealCycle(Dialect.ORACLE, "customer.create_time", "contract.order_date"));
        // SQL Server：DATEDIFF(DAY) 按时刻向零修正，DECIMAL 平均，ROUND(..., 1, 1) 截断
        String beginTime = "CAST(CAST(customer.create_time AS datetime2(7)) AS time(7))";
        String endTime = "CAST(CAST(contract.order_date AS datetime2(7)) AS time(7))";
        String days = "DATEDIFF(DAY, customer.create_time, contract.order_date)";
        assertEquals("COALESCE(ROUND(AVG(CAST((CASE WHEN contract.order_date >= customer.create_time THEN "
                        + days + " - CASE WHEN " + endTime + " < " + beginTime + " THEN 1 ELSE 0 END ELSE "
                        + days + " + CASE WHEN " + endTime + " > " + beginTime + " THEN 1 ELSE 0 END END)"
                        + " AS DECIMAL(19, 4))), 1, 1), 0)",
                SqlDialectUtils.customerDealCycle(Dialect.SQLSERVER, "customer.create_time", "contract.order_date"));
    }

    @Test
    public void testFromDual() {
        assertEquals("", SqlDialectUtils.fromDual(Dialect.ORIGINAL));
        assertEquals("", SqlDialectUtils.fromDual(Dialect.POSTGRESQL));
        assertEquals("FROM DUAL", SqlDialectUtils.fromDual(Dialect.ORACLE));
        assertEquals("", SqlDialectUtils.fromDual(Dialect.SQLSERVER));
    }

    @Test
    public void testInvalidColumnName() {
        List<BiFunction<Dialect, String, String>> fragments = List.of(SqlDialectUtils::formatDate,
                SqlDialectUtils::formatYearMonth, SqlDialectUtils::formatYearDashMonth, SqlDialectUtils::date,
                SqlDialectUtils::castDateText, SqlDialectUtils::castDate, SqlDialectUtils::month,
                SqlDialectUtils::year, SqlDialectUtils::dayOfMonth);
        for (BiFunction<Dialect, String, String> fragment : fragments) {
            for (Dialect dialect : Dialect.values()) {
                assertThrows(IllegalArgumentException.class, () -> fragment.apply(dialect, "create_time) OR (1=1"));
                assertThrows(IllegalArgumentException.class, () -> fragment.apply(dialect, "create_time;drop table t"));
                assertThrows(IllegalArgumentException.class, () -> fragment.apply(dialect, "'2026-01-01'"));
                assertThrows(IllegalArgumentException.class, () -> fragment.apply(dialect, ""));
                assertThrows(IllegalArgumentException.class, () -> fragment.apply(dialect, null));
            }
        }
        for (Dialect dialect : Dialect.values()) {
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.customerDealCycle(dialect, "customer.create_time", "NOW()"));
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.customerDealCycle(dialect, "1) - (1", "contract.order_date"));
        }
    }

    /**
     * MyBatis Plus 租户等插件会使用 JSqlParser 解析最终 SQL，所有方言的片段均需可被解析
     */
    @Test
    public void testFragments_parsedByJSqlParser() throws Exception {
        for (Dialect dialect : Dialect.values()) {
            List<String> expressions = List.of(
                    SqlDialectUtils.formatDate(dialect, "t.create_time"),
                    SqlDialectUtils.formatYearMonth(dialect, "t.create_time"),
                    SqlDialectUtils.formatYearDashMonth(dialect, "t.create_time"),
                    SqlDialectUtils.date(dialect, "t.create_time"),
                    SqlDialectUtils.castDateText(dialect, "t.create_time"),
                    SqlDialectUtils.castDate(dialect, "t.create_time"),
                    SqlDialectUtils.month(dialect, "t.create_time"),
                    SqlDialectUtils.year(dialect, "t.create_time"),
                    SqlDialectUtils.dayOfMonth(dialect, "t.create_time"));
            for (String expression : expressions) {
                String sql = "SELECT " + expression + " AS statDate, COUNT(1) AS total FROM crm_customer t"
                        + " WHERE t.create_time >= ? GROUP BY " + expression + " ORDER BY statDate";
                assertNotNull(CCJSqlParserUtil.parse(sql), dialect + " " + expression);
            }
            String dealCycleSql = "SELECT customer.owner_user_id, "
                    + SqlDialectUtils.customerDealCycle(dialect, "customer.create_time", "contract.order_date")
                    + " AS customer_deal_cycle FROM crm_customer customer"
                    + " LEFT JOIN crm_contract contract ON contract.customer_id = customer.id"
                    + " GROUP BY customer.owner_user_id";
            assertNotNull(CCJSqlParserUtil.parse(dealCycleSql), dialect + " customerDealCycle");
            String summarySql = "SELECT (SELECT COUNT(1) FROM ai1_agent) AS agentCount, "
                    + "(SELECT COUNT(1) FROM ai1_model) AS modelCount " + SqlDialectUtils.fromDual(dialect);
            assertNotNull(CCJSqlParserUtil.parse(summarySql), dialect + " fromDual");
        }
    }

    // ========== 逗号分隔列表 ==========

    @Test
    public void testFindInSetColumn() {
        // 原有片段保持不变，不追加 <> 0
        assertEquals("FIND_IN_SET(cc.id, cfur.contact_ids)",
                SqlDialectUtils.findInSetColumn(Dialect.ORIGINAL, "cc.id", "cfur.contact_ids"));
        assertEquals("POSITION(',' || CAST(cc.id AS VARCHAR) || ',' IN ',' || cfur.contact_ids || ',') > 0",
                SqlDialectUtils.findInSetColumn(Dialect.POSTGRESQL, "cc.id", "cfur.contact_ids"));
        assertEquals("(cc.id IS NOT NULL AND INSTR(',' || cfur.contact_ids || ',', ',' || cc.id || ',') > 0)",
                SqlDialectUtils.findInSetColumn(Dialect.ORACLE, "cc.id", "cfur.contact_ids"));
        assertEquals("(cc.id IS NOT NULL AND CHARINDEX(',' + CAST(cc.id AS varchar(20)) + ',', ',' + cfur.contact_ids + ',') > 0)",
                SqlDialectUtils.findInSetColumn(Dialect.SQLSERVER, "cc.id", "cfur.contact_ids"));
        // 不带表别名、以 _id 结尾的 ID 列
        assertEquals("FIND_IN_SET(contact_id, contact_ids)",
                SqlDialectUtils.findInSetColumn(Dialect.ORIGINAL, "contact_id", "contact_ids"));
        assertEquals("(t.CONTACT_ID IS NOT NULL AND CHARINDEX(',' + CAST(t.CONTACT_ID AS varchar(20)) + ',', ',' + t.contact_ids + ',') > 0)",
                SqlDialectUtils.findInSetColumn(Dialect.SQLSERVER, "t.CONTACT_ID", "t.contact_ids"));
    }

    @Test
    public void testFindInSetColumn_invalidColumn() {
        for (Dialect dialect : Dialect.values()) {
            // 值列：只能是 ID 列名，不能是表达式、字面量或业务字符串列
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.findInSetColumn(dialect, "cc.id) OR (1=1", "cfur.contact_ids"));
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.findInSetColumn(dialect, "'1'", "cfur.contact_ids"));
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.findInSetColumn(dialect, "1", "cfur.contact_ids"));
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.findInSetColumn(dialect, "cc.name", "cfur.contact_ids"));
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.findInSetColumn(dialect, "cc.idx", "cfur.contact_ids"));
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.findInSetColumn(dialect, "id.name", "cfur.contact_ids"));
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.findInSetColumn(dialect, "", "cfur.contact_ids"));
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.findInSetColumn(dialect, null, "cfur.contact_ids"));
            // 列表列
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.findInSetColumn(dialect, "cc.id", "cfur.contact_ids) OR (1=1"));
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.findInSetColumn(dialect, "cc.id", "'1,2,3'"));
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.findInSetColumn(dialect, "cc.id", "cfur.contact_ids;drop table t"));
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.findInSetColumn(dialect, "cc.id", ""));
            assertThrows(IllegalArgumentException.class,
                    () -> SqlDialectUtils.findInSetColumn(dialect, "cc.id", null));
        }
    }

    /**
     * 与 CRM 跟进次数排行相同的 LEFT JOIN 条件，所有方言均需可被 JSqlParser 解析
     */
    @Test
    public void testFindInSetColumn_parsedByJSqlParser() throws Exception {
        for (Dialect dialect : Dialect.values()) {
            String sql = "SELECT COUNT(DISTINCT cc.id) AS count, cc.owner_user_id FROM crm_follow_up_record cfur"
                    + " LEFT JOIN crm_contact cc ON " + SqlDialectUtils.findInSetColumn(dialect, "cc.id", "cfur.contact_ids")
                    + " WHERE cfur.deleted = 0 AND cc.deleted = 0 AND cc.owner_user_id IN (?, ?)"
                    + " AND cfur.create_time BETWEEN ? AND ? GROUP BY cc.owner_user_id";
            assertNotNull(CCJSqlParserUtil.parse(sql), dialect + " " + sql);
        }
    }


    // ========== JSON 数组 ==========

    @Test
    public void testJsonArrayContains() {
        // 原有片段：保持原有 JSON_CONTAINS
        assertEquals("JSON_CONTAINS(booking_user_ids, {0})",
                SqlDialectUtils.jsonArrayContains(Dialect.ORIGINAL, "booking_user_ids", false));
        assertEquals("JSON_CONTAINS(t.label_ids, JSON_ARRAY({0}))",
                SqlDialectUtils.jsonArrayContains(Dialect.ORIGINAL, "t.label_ids", true));
        // PostgreSQL：两种原有片段统一为 jsonb 包含，参数按 BIGINT 匹配数字元素
        assertEquals(postgreSQLJsonArrayContains("booking_user_ids"),
                SqlDialectUtils.jsonArrayContains(Dialect.POSTGRESQL, "booking_user_ids", false));
        assertEquals(postgreSQLJsonArrayContains("t.label_ids"),
                SqlDialectUtils.jsonArrayContains(Dialect.POSTGRESQL, "t.label_ids", true));
        // PostgreSQL：{0} 只出现一次；openGauss、GaussDB 没有 jsonb_build_array，
        // openGauss 的 A、B 兼容模式下 || 会忽略 NULL 或作为逻辑或，所以不使用；null 参数由 COALESCE 拼成 [null]
        String postgreSQL = SqlDialectUtils.jsonArrayContains(Dialect.POSTGRESQL, "t.label_ids", true);
        assertEquals(1, postgreSQL.split("\\{0}", -1).length - 1, postgreSQL);
        assertTrue(postgreSQL.contains("CAST({0} AS BIGINT)"), postgreSQL);
        assertTrue(postgreSQL.contains("COALESCE(CAST(CAST({0} AS BIGINT) AS TEXT), 'null')"), postgreSQL);
        assertFalse(postgreSQL.contains("jsonb_build_array"), postgreSQL);
        assertFalse(postgreSQL.contains("||"), postgreSQL);
        // Oracle、SQL Server、H2：两种原有片段相同，{0} 只出现一次，不使用 JSON 函数
        for (Dialect dialect : List.of(Dialect.ORACLE, Dialect.SQLSERVER, Dialect.H2)) {
            String condition = SqlDialectUtils.jsonArrayContains(dialect, "t.label_ids", true);
            assertEquals(condition, SqlDialectUtils.jsonArrayContains(dialect, "t.label_ids", false), dialect.name());
            assertEquals(1, condition.split("\\{0}", -1).length - 1, condition);
            assertFalse(condition.toUpperCase().contains("JSON"), condition);
        }
        String oracle = SqlDialectUtils.jsonArrayContains(Dialect.ORACLE, "t.label_ids", true);
        // CLOB 超过 4000 字节时 TRANSLATE 报 ORA-22835，空白只能用 REPLACE 替换
        assertTrue(oracle.contains("REGEXP_LIKE(REPLACE(REPLACE(REPLACE(t.label_ids, CHR(9), ' '), CHR(10), ' '), CHR(13), ' '), "),
                oracle);
        assertFalse(oracle.toUpperCase().contains("TRANSLATE"), oracle);
        assertTrue(oracle.contains("'" + SqlDialectUtils.ORACLE_LONG_ARRAY_PATTERN + "'"), oracle);
        assertFalse(oracle.contains(SqlDialectUtils.H2_LONG_ARRAY_PATTERN), oracle);
        assertTrue(oracle.contains("NOT REGEXP_LIKE(t.label_ids, '" + SqlDialectUtils.LONG_OVERFLOW_PATTERN + "')"), oracle);
        assertTrue(oracle.contains("COALESCE(TO_CHAR(CAST({0} AS NUMBER(19, 0)), 'TM9', "
                + "'NLS_NUMERIC_CHARACTERS = ''.,'''), 'null')"), oracle);
        // SQL Server：与原生验收的 SQL 一致；XML 用 .query('.') 物化后整体 exist 校验，再按逗号边界 CHARINDEX 匹配
        String sqlServer = SqlDialectUtils.jsonArrayContains(Dialect.SQLSERVER, "t.label_ids", true);
        assertEquals("EXISTS (SELECT 1"
                + " FROM (SELECT LTRIM(RTRIM(REPLACE(REPLACE(REPLACE(CAST(t.label_ids AS nvarchar(max)),CHAR(9),N' '),CHAR(10),N' '),CHAR(13),N' '))) AS doc) yd_jd"
                + " CROSS APPLY (SELECT CASE WHEN LEFT(yd_jd.doc,1)=N'[' AND RIGHT(yd_jd.doc,1)=N']'"
                + " THEN SUBSTRING(yd_jd.doc,2,CASE WHEN LEN(yd_jd.doc)>=2"
                + " THEN LEN(yd_jd.doc)-2 ELSE 0 END) ELSE NULL END AS body) yd_jb"
                + " CROSS APPLY (SELECT CAST(CASE WHEN yd_jb.body IS NOT NULL"
                + " AND PATINDEX(N'%[^0-9,nul -]%',COALESCE(yd_jb.body,N'') COLLATE Latin1_General_100_BIN2)=0"
                + " AND LTRIM(RTRIM(yd_jb.body))<>N''"
                + " THEN N'<r><i>'+REPLACE(yd_jb.body,N',',N'</i><i>')+N'</i></r>' ELSE N'<r/>' END AS XML) AS tokens) yd_jx"
                + " CROSS APPLY (SELECT yd_jx.tokens.query('.') AS tokens) yd_jm"
                + " WHERE yd_jm.tokens.exist('/r[i and not(i[string-length(string(xs:token(string(.)))) = 0"
                + " or (string(xs:token(string(.))) != \"null\""
                + " and string(xs:token(string(.))) != string((string(xs:token(string(.))) cast as xs:long?)))])]') = 1"
                + " AND CHARINDEX(N','+COALESCE(CONVERT(nvarchar(20),CAST({0} AS BIGINT)),N'null')+N',',N','+REPLACE(yd_jb.body,N' ',N'')+N',')>0)",
                sqlServer);
        // SQL Server 2012~2016 不支持 OPENJSON，任何分支都不能出现；不逐个遍历节点，XQuery 中不嵌入业务列
        assertFalse(sqlServer.toUpperCase().contains("OPENJSON"), sqlServer);
        assertFalse(sqlServer.contains(".nodes("), sqlServer);
        assertFalse(sqlServer.contains("sql:column"), sqlServer);
        // H2：与原生验收的 SQL 一致，使用 H2 专用的正则（Java 正则的占有量词），不使用 Oracle 的正则
        String h2 = SqlDialectUtils.jsonArrayContains(Dialect.H2, "t.label_ids", true);
        assertEquals("^ *+\\[ *+(?:(?:0|-?[1-9][0-9]{0,18}|null) *+(?:, *+(?:0|-?[1-9][0-9]{0,18}|null) *+)*+)?\\] *+$",
                SqlDialectUtils.H2_LONG_ARRAY_PATTERN);
        assertEquals("(REGEXP_LIKE(REPLACE(REPLACE(REPLACE(t.label_ids, CHR(9), ' '), CHR(10), ' '), CHR(13), ' '), '"
                        + SqlDialectUtils.H2_LONG_ARRAY_PATTERN + "')"
                        + " AND NOT REGEXP_LIKE(t.label_ids, '" + SqlDialectUtils.LONG_OVERFLOW_PATTERN + "')"
                        + " AND LOCATE(',' || COALESCE(CAST(CAST({0} AS BIGINT) AS VARCHAR), 'null') || ',',"
                        + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(t.label_ids, CHR(9), ' '), CHR(10), ' '), CHR(13), ' '),"
                        + " ' ', ''), '[', ','), ']', ',')) > 0)",
                h2);
        assertFalse(h2.contains(SqlDialectUtils.ORACLE_LONG_ARRAY_PATTERN), h2);
    }

    /**
     * Oracle 片段的匹配语义：按 REGEXP_LIKE、REPLACE、INSTR 的组合在 Java 中复现，校验严格的平面 Long / null 数组契约
     */
    @Test
    public void testJsonArrayContains_oracleLongArraySemantics() {
        // 普通匹配：相邻 ID、负数、大 ID、Long 边界、空白
        assertTrue(oracleJsonContains("[1,11]", 1L));
        assertTrue(oracleJsonContains("[1,11]", 11L));
        assertFalse(oracleJsonContains("[11,21]", 1L));
        assertTrue(oracleJsonContains("[2115311103682412546,2115311103682412547]", 2115311103682412546L));
        assertFalse(oracleJsonContains("[2115311103682412546]", 211531110368241254L));
        assertTrue(oracleJsonContains("[-1, 1]", -1L));
        assertFalse(oracleJsonContains("[-1]", 1L));
        assertTrue(oracleJsonContains("[" + Long.MIN_VALUE + "," + Long.MAX_VALUE + "]", Long.MIN_VALUE));
        assertTrue(oracleJsonContains("[" + Long.MIN_VALUE + "," + Long.MAX_VALUE + "]", Long.MAX_VALUE));
        assertTrue(oracleJsonContains(" [ 1 ,\t2 ,\r\n 3 ] ", 2L));
        assertTrue(oracleJsonContains("[0]", 0L));
        // null 元素：参数为 null 时匹配 null 元素
        assertTrue(oracleJsonContains("[null]", null));
        assertTrue(oracleJsonContains("[1, null]", null));
        assertTrue(oracleJsonContains("[1, null]", 1L));
        assertFalse(oracleJsonContains("[1]", null));
        assertFalse(oracleJsonContains("[NULL]", null));
        // 非数组、空数组、列为 NULL
        assertFalse(oracleJsonContains(null, 1L));
        assertFalse(oracleJsonContains(null, null));
        assertFalse(oracleJsonContains("null", null));
        assertFalse(oracleJsonContains("[]", 1L));
        assertFalse(oracleJsonContains("[ ]", null));
        assertFalse(oracleJsonContains("1", 1L));
        // 字符串、嵌套、对象、小数、指数、前导零、空元素、缺分隔符：整体不匹配
        assertFalse(oracleJsonContains("[\"1\"]", 1L));
        assertFalse(oracleJsonContains("[\"x,1,y\"]", 1L));
        assertFalse(oracleJsonContains("[1,\"x\"]", 1L));
        assertFalse(oracleJsonContains("[[1]]", 1L));
        assertFalse(oracleJsonContains("[1,[2]]", 1L));
        assertFalse(oracleJsonContains("[{\"a\":1}]", 1L));
        assertFalse(oracleJsonContains("[1.0]", 1L));
        assertFalse(oracleJsonContains("[1e3]", 1000L));
        assertFalse(oracleJsonContains("[01]", 1L));
        assertFalse(oracleJsonContains("[-0]", 0L));
        assertFalse(oracleJsonContains("[1,,2]", 1L));
        assertFalse(oracleJsonContains("[,1]", 1L));
        assertFalse(oracleJsonContains("[1,]", 1L));
        assertFalse(oracleJsonContains("[1 2]", 12L));
        assertFalse(oracleJsonContains("[1 2]", 1L));
        assertFalse(oracleJsonContains("[1]]", 1L));
        // 超出 Long 范围：整个数组不匹配
        assertFalse(oracleJsonContains("[9223372036854775808,1]", 1L));
        assertFalse(oracleJsonContains("[-9223372036854775809,1]", 1L));
        assertFalse(oracleJsonContains("[9999999999999999999,1]", 1L));
        assertFalse(oracleJsonContains("[99999999999999999999,1]", 1L));
        assertTrue(oracleJsonContains("[-9223372036854775808,1]", 1L));
        assertTrue(oracleJsonContains("[9223372036854775807,1]", 1L));
        assertTrue(oracleJsonContains("[1000000000000000000,1]", 1L));
        // 长 CLOB 数组由 Oracle 实库验证；Java Pattern 的递归栈不代表 Oracle 的正则执行能力。
        // H2 使用 Java 正则，其长数组边界由 testJsonArrayContains_h2LongArraySemantics 验证。
    }

    /**
     * H2 片段的匹配语义：H2 的 REGEXP_LIKE 使用 Java 正则，H2 专用的正则在长数组上不能栈溢出
     *
     * 注意：仅校验正则与匹配规则，不代替 H2 实库验收
     */
    @Test
    public void testJsonArrayContains_h2LongArraySemantics() {
        // 普通匹配、null 元素、空白
        assertTrue(h2JsonContains("[1,11]", 1L));
        assertFalse(h2JsonContains("[11,21]", 1L));
        assertTrue(h2JsonContains("[2115311103682412546,2115311103682412547]", 2115311103682412547L));
        assertTrue(h2JsonContains("[" + Long.MIN_VALUE + "," + Long.MAX_VALUE + "]", Long.MIN_VALUE));
        assertTrue(h2JsonContains(" [ 1 ,\t2 ,\r\n 3 ] ", 2L));
        assertTrue(h2JsonContains("[1, null]", null));
        assertFalse(h2JsonContains("[1]", null));
        // 非数组、字符串、嵌套、小数、前导零、空元素、缺分隔符、超出 Long 范围：整体不匹配
        assertFalse(h2JsonContains(null, 1L));
        assertFalse(h2JsonContains("null", null));
        assertFalse(h2JsonContains("[]", 1L));
        assertFalse(h2JsonContains("[\"1\"]", 1L));
        assertFalse(h2JsonContains("[\"x,1,y\"]", 1L));
        assertFalse(h2JsonContains("[[1]]", 1L));
        assertFalse(h2JsonContains("[1.0]", 1L));
        assertFalse(h2JsonContains("[01]", 1L));
        assertFalse(h2JsonContains("[1,,2]", 1L));
        assertFalse(h2JsonContains("[1 2]", 1L));
        assertFalse(h2JsonContains("[9223372036854775808,1]", 1L));
        assertFalse(h2JsonContains("[-9223372036854775809,1]", 1L));
        // 1400 个元素的长数组：占有量词不回溯、不递归，不能栈溢出；末尾非法元素整体不匹配
        StringBuilder longArray = new StringBuilder("[0");
        for (int i = 1; i < 1400; i++) {
            longArray.append(i % 2 == 0 ? ",\n" : " , ").append(2115311103682412546L + i);
        }
        String longJson = longArray.append(']').toString();
        assertTrue(h2JsonContains(longJson, 2115311103682412546L + 1399));
        assertFalse(h2JsonContains(longJson, 2115311103682412546L + 1400));
        assertFalse(h2JsonContains(longJson.replace("]", ",\"x\"]"), 0L));
    }

    @Test
    public void testGreaterDigitsPattern() {
        assertEquals("(1[9-9]|[2-9][0-9]{1})", SqlDialectUtils.greaterDigitsPattern("18"));
        Pattern pattern = Pattern.compile(SqlDialectUtils.greaterDigitsPattern("9223372036854775808"));
        assertTrue(pattern.matcher("9223372036854775809").matches());
        assertTrue(pattern.matcher("9223372036854775810").matches());
        assertTrue(pattern.matcher("9300000000000000000").matches());
        assertTrue(pattern.matcher("9999999999999999999").matches());
        assertFalse(pattern.matcher("9223372036854775808").matches());
        assertFalse(pattern.matcher("9223372036854775807").matches());
        assertFalse(pattern.matcher("8999999999999999999").matches());
        assertFalse(pattern.matcher("1000000000000000000").matches());
        // Oracle 正则最长 512 字节；也不能包含会被 Wrapper 当作占位符的 {0}
        assertTrue(SqlDialectUtils.LONG_OVERFLOW_PATTERN.length() < 512);
        assertTrue(SqlDialectUtils.ORACLE_LONG_ARRAY_PATTERN.length() < 512);
        assertFalse(SqlDialectUtils.LONG_OVERFLOW_PATTERN.contains("{0}"));
        assertFalse(SqlDialectUtils.H2_LONG_ARRAY_PATTERN.contains("{0}"));
    }

    @Test
    public void testResolveJsonDialect() throws SQLException {
        // 瀚高原生驱动：仅 JSON 片段使用 PostgreSQL 写法，其它片段仍保持原有
        assertEquals(Dialect.POSTGRESQL, resolveJson("HighGo", "jdbc:highgo://127.0.0.1:5866/highgo", 6));
        assertEquals(Dialect.POSTGRESQL, resolveJson("PostgreSQL", "jdbc:highgo://127.0.0.1:5866/highgo", 6));
        assertEquals(Dialect.ORIGINAL, resolve("PostgreSQL", "jdbc:highgo://127.0.0.1:5866/highgo", 6));
        // 人大金仓（产品名 KingbaseES）：仅 JSON 片段使用 PostgreSQL 写法，其它片段仍保持原有
        assertEquals(Dialect.POSTGRESQL, resolveJson("KingbaseES", "jdbc:kingbase8://127.0.0.1:54321/test", 8));
        assertEquals(Dialect.ORIGINAL, resolve("KingbaseES", "jdbc:kingbase8://127.0.0.1:54321/test", 8));
        // H2：仅 JSON 片段使用严格的平面 Long 数组写法，其它片段仍保持原有
        assertEquals(Dialect.H2, resolveJson("H2", "jdbc:h2:mem:testdb;MODE=MYSQL", 2));
        assertEquals(Dialect.ORIGINAL, resolve("H2", "jdbc:h2:mem:testdb;MODE=MYSQL", 2));
        // openGauss、GaussDB：仅 JSON 片段使用 PostgreSQL 写法，见 testResolveJsonDialect_openGauss
        assertEquals(Dialect.POSTGRESQL, resolveJson("PostgreSQL", "jdbc:opengauss://127.0.0.1:5432/test", 9));
        assertEquals(Dialect.POSTGRESQL, resolveJson("openGauss", "jdbc:opengauss://127.0.0.1:5432/test", 5));
        // 其它 PostgreSQL 变种（含产品名为 PostgreSQL 的金仓 URL）、达梦、神通、MySQL：保持原有 JSON_CONTAINS
        assertEquals(Dialect.ORIGINAL, resolveJson("PostgreSQL", "jdbc:kingbase8://127.0.0.1:54321/test", 12));
        assertEquals(Dialect.ORIGINAL, resolveJson("DM DBMS", "jdbc:dm://127.0.0.1:5236", 8));
        assertEquals(Dialect.ORIGINAL, resolveJson("OSCAR", "jdbc:oscar://127.0.0.1:2003/osrdb", 7));
        assertEquals(Dialect.ORIGINAL, resolveJson("MySQL", "jdbc:mysql://127.0.0.1:3306/highgo", 8));
        assertEquals(Dialect.ORIGINAL, resolveJson("Microsoft SQL Server 2005", "jdbc:jtds:sqlserver://127.0.0.1:1433", 9));
        // 其它与 resolveDialect 一致
        assertEquals(Dialect.POSTGRESQL, resolveJson("PostgreSQL", "jdbc:postgresql://127.0.0.1:5432/a", 17));
        assertEquals(Dialect.ORACLE, resolveJson("Oracle", "jdbc:oracle:thin:@127.0.0.1:1521:xe", 21));
        assertEquals(Dialect.SQLSERVER, resolveJson("Microsoft SQL Server", "jdbc:sqlserver://127.0.0.1:1433", 11));
        assertThrows(IllegalStateException.class, () -> resolveJson("SQLite", "jdbc:sqlite:/tmp/test.db", 3));
    }

    /**
     * openGauss、GaussDB（含 MogDB）：JSON 片段与普通片段的分类有意不同
     *
     * 项目要求使用 PG 兼容模式，原有 JSON_CONTAINS 不存在或匹配不精确，所以 JSON 片段按 PostgreSQL 处理；
     * 日期、行数限制等普通片段未验收，仍保持原有片段
     */
    @Test
    public void testResolveJsonDialect_openGauss() throws SQLException {
        // 原生产品名：openGauss（含 openGauss 原生驱动连接的 MogDB）、GaussDB
        assertEquals(Dialect.POSTGRESQL, resolveJson("openGauss", "jdbc:opengauss://127.0.0.1:15434/postgres", 9));
        assertEquals(Dialect.ORIGINAL, resolve("openGauss", "jdbc:opengauss://127.0.0.1:15434/postgres", 9));
        assertEquals(Dialect.POSTGRESQL, resolveJson("GaussDB", "jdbc:gaussdb://127.0.0.1:8000/postgres", 9));
        assertEquals(Dialect.ORIGINAL, resolve("GaussDB", "jdbc:gaussdb://127.0.0.1:8000/postgres", 9));
        // 产品名为 PostgreSQL 或未登记时，按原生驱动 URL 识别，不区分大小写
        for (String url : List.of("jdbc:opengauss://127.0.0.1:5432/test", "jdbc:gaussdb://127.0.0.1:8000/test",
                "jdbc:mogdb://127.0.0.1:5432/test", "JDBC:OPENGAUSS://127.0.0.1:5432/test")) {
            assertEquals(Dialect.POSTGRESQL, resolveJson("PostgreSQL", url, 9), url);
            assertEquals(Dialect.POSTGRESQL, resolveJson("MogDB", url, 5), url);
            assertEquals(Dialect.ORIGINAL, resolve("PostgreSQL", url, 9), url);
        }
        // URL 只按开头的协议识别：库名中出现 opengauss 的 MySQL 仍保持原有片段
        assertEquals(Dialect.ORIGINAL, resolveJson("MySQL", "jdbc:mysql://127.0.0.1:3306/opengauss", 8));
        assertEquals(Dialect.ORIGINAL, resolveJson("DM DBMS", "jdbc:dm://127.0.0.1:5236?schema=gaussdb", 8));
    }

    @Test
    public void testJsonArrayContains_invalidColumn() {
        for (Dialect dialect : Dialect.values()) {
            for (boolean wrapArray : new boolean[]{true, false}) {
                assertThrows(IllegalArgumentException.class,
                        () -> SqlDialectUtils.jsonArrayContains(dialect, "label_ids, '[1]') OR (1=1", wrapArray));
                assertThrows(IllegalArgumentException.class,
                        () -> SqlDialectUtils.jsonArrayContains(dialect, "'[1, 2]'", wrapArray));
                assertThrows(IllegalArgumentException.class,
                        () -> SqlDialectUtils.jsonArrayContains(dialect, "label_ids;drop table t", wrapArray));
                assertThrows(IllegalArgumentException.class,
                        () -> SqlDialectUtils.jsonArrayContains(dialect, "", wrapArray));
                assertThrows(IllegalArgumentException.class,
                        () -> SqlDialectUtils.jsonArrayContains(dialect, null, wrapArray));
            }
        }
    }

    /**
     * 与 PMS 工作项任一标签筛选相同的 OR 条件，所有方言均需可被 JSqlParser 解析
     */
    @Test
    public void testJsonArrayContains_parsedByJSqlParser() throws Exception {
        for (Dialect dialect : Dialect.values()) {
            for (boolean wrapArray : new boolean[]{true, false}) {
                String condition = SqlDialectUtils.jsonArrayContains(dialect, "label_ids", wrapArray).replace("{0}", "?");
                String sql = "SELECT id FROM pms_work_item WHERE project_id = ? AND (" + condition + " OR " + condition + ")";
                assertNotNull(CCJSqlParserUtil.parse(sql), dialect + " " + sql);
            }
        }
    }

    /**
     * 与 Mapper 相同：通过 MyBatis Plus Wrapper 的 apply 绑定参数，再由 MyBatis 生成 BoundSql
     */

    /**
     * openGauss、GaussDB 路由到 PostgreSQL 的 JSON 片段：超出 2^53 的相邻编号仍各自作为参数绑定，按 BIGINT 比较。
     * 这里只验证片段与参数结构，数据库中的实际匹配结果需在实库验收
     */

    // ========== 行数限制 ==========

    @Test
    public void testLimitRows() {
        assertEquals("LIMIT 1", SqlDialectUtils.limitRows(Dialect.ORIGINAL, 1, false));
        assertEquals("LIMIT 10", SqlDialectUtils.limitRows(Dialect.POSTGRESQL, 10, true));
        assertEquals("FETCH FIRST 1 ROWS ONLY", SqlDialectUtils.limitRows(Dialect.ORACLE, 1, false));
        assertEquals("FETCH FIRST 10 ROWS ONLY", SqlDialectUtils.limitRows(Dialect.ORACLE, 10, true));
        // SQL Server：OFFSET 必须跟在 ORDER BY 之后，无排序时补充常量排序
        assertEquals("OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY", SqlDialectUtils.limitRows(Dialect.SQLSERVER, 10, true));
        assertEquals("ORDER BY (SELECT NULL) OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY",
                SqlDialectUtils.limitRows(Dialect.SQLSERVER, 1, false));
    }

    @Test
    public void testLimitRows_zeroAndNegative() {
        assertEquals("LIMIT 0", SqlDialectUtils.limitRows(Dialect.ORIGINAL, 0, true));
        assertEquals("LIMIT 0", SqlDialectUtils.limitRows(Dialect.POSTGRESQL, 0, true));
        assertEquals("FETCH FIRST 0 ROWS ONLY", SqlDialectUtils.limitRows(Dialect.ORACLE, 0, true));
        // SQL Server 的 FETCH 不支持 0 行
        assertThrows(IllegalArgumentException.class, () -> SqlDialectUtils.limitRows(Dialect.SQLSERVER, 0, true));
        for (Dialect dialect : Dialect.values()) {
            assertThrows(IllegalArgumentException.class, () -> SqlDialectUtils.limitRows(dialect, -1, true));
        }
    }

    @Test
    public void testLimitParam() {
        assertEquals("LIMIT #{limit}", SqlDialectUtils.limitParam(Dialect.ORIGINAL, "limit", true));
        assertEquals("LIMIT #{reqVO.limit}", SqlDialectUtils.limitParam(Dialect.POSTGRESQL, "reqVO.limit", true));
        assertEquals("FETCH FIRST #{limit} ROWS ONLY", SqlDialectUtils.limitParam(Dialect.ORACLE, "limit", false));
        assertEquals("OFFSET 0 ROWS FETCH NEXT #{limit} ROWS ONLY", SqlDialectUtils.limitParam(Dialect.SQLSERVER, "limit", true));
        assertEquals("ORDER BY (SELECT NULL) OFFSET 0 ROWS FETCH NEXT #{limit} ROWS ONLY",
                SqlDialectUtils.limitParam(Dialect.SQLSERVER, "limit", false));
        // 参数名只能是标识符或属性路径
        for (Dialect dialect : Dialect.values()) {
            assertThrows(IllegalArgumentException.class, () -> SqlDialectUtils.limitParam(dialect, "limit} OR 1=1 #{a", true));
            assertThrows(IllegalArgumentException.class, () -> SqlDialectUtils.limitParam(dialect, "1", true));
            assertThrows(IllegalArgumentException.class, () -> SqlDialectUtils.limitParam(dialect, "", true));
            assertThrows(IllegalArgumentException.class, () -> SqlDialectUtils.limitParam(dialect, null, true));
        }
    }

    // ========== 分批删除 ==========

    @Test
    public void testDeleteBatchByLessThan() {
        // 原有语句保持不变
        assertEquals("DELETE FROM infra_job_log WHERE create_time < #{createTime} LIMIT #{limit}",
                SqlDialectUtils.deleteBatchByLessThan(Dialect.ORIGINAL, "infra_job_log", "create_time", "createTime", "limit"));
        assertEquals("DELETE FROM infra_job_log WHERE id IN (SELECT id FROM infra_job_log"
                        + " WHERE create_time < #{createTime} LIMIT #{limit})",
                SqlDialectUtils.deleteBatchByLessThan(Dialect.POSTGRESQL, "infra_job_log", "create_time", "createTime", "limit"));
        assertEquals("DELETE FROM system_oauth2_access_token WHERE expires_time < #{expiresTime} AND ROWNUM <= #{limit}",
                SqlDialectUtils.deleteBatchByLessThan(Dialect.ORACLE, "system_oauth2_access_token", "expires_time", "expiresTime", "limit"));
        assertEquals("DELETE FROM system_oauth2_access_token WHERE id IN (SELECT TOP (#{limit}) id FROM system_oauth2_access_token"
                        + " WHERE expires_time < #{expiresTime})",
                SqlDialectUtils.deleteBatchByLessThan(Dialect.SQLSERVER, "system_oauth2_access_token", "expires_time", "expiresTime", "limit"));
    }

    @Test
    public void testDeleteBatchByLessThan_invalidName() {
        for (Dialect dialect : Dialect.values()) {
            assertThrows(IllegalArgumentException.class, () -> SqlDialectUtils.deleteBatchByLessThan(dialect,
                    "infra_job_log; DROP TABLE t", "create_time", "createTime", "limit"));
            assertThrows(IllegalArgumentException.class, () -> SqlDialectUtils.deleteBatchByLessThan(dialect,
                    "infra_job_log", "create_time) OR (1=1", "createTime", "limit"));
            assertThrows(IllegalArgumentException.class, () -> SqlDialectUtils.deleteBatchByLessThan(dialect,
                    "infra_job_log", "create_time", "createTime} OR 1=1 OR #{a", "limit"));
            assertThrows(IllegalArgumentException.class, () -> SqlDialectUtils.deleteBatchByLessThan(dialect,
                    "infra_job_log", "create_time", "createTime", null));
        }
    }

    /**
     * MyBatis Plus 租户、数据权限插件会使用 JSqlParser 解析最终 SQL，所有方言的语句均需可被解析
     *
     * 注意：JSqlParser 5.2 不支持 DELETE TOP (?)，因此 SQL Server 使用主键子查询
     */
    @Test
    public void testLimitAndDelete_parsedByJSqlParser() throws Exception {
        for (Dialect dialect : Dialect.values()) {
            String deleteSql = SqlDialectUtils.deleteBatchByLessThan(dialect, "infra_job_log", "create_time", "createTime", "limit");
            assertNotNull(CCJSqlParserUtil.parse(bind(deleteSql)), dialect + " " + deleteSql);

            String orderedSql = "SELECT user_id AS userId, COUNT(*) AS messageCount FROM im_private_message"
                    + " WHERE send_time >= ? GROUP BY user_id ORDER BY messageCount DESC "
                    + SqlDialectUtils.limitParam(dialect, "limit", true);
            assertNotNull(CCJSqlParserUtil.parse(bind(orderedSql)), dialect + " " + orderedSql);
            String unorderedSql = "SELECT * FROM hrm_salary_option WHERE code = ? "
                    + SqlDialectUtils.limitRows(dialect, 1, false);
            assertNotNull(CCJSqlParserUtil.parse(unorderedSql), dialect + " " + unorderedSql);
        }
    }

    // ========== 方言识别 ==========

    @Test
    public void testResolveDialect_accepted() throws SQLException {
        assertEquals(Dialect.POSTGRESQL, resolve("PostgreSQL", "jdbc:postgresql://127.0.0.1:5432/ruoyi-vue-pro", 17));
        assertEquals(Dialect.POSTGRESQL, resolve("PostgreSQL", "jdbc:p6spy:postgresql://127.0.0.1:5432/ruoyi-vue-pro", 17));
        assertEquals(Dialect.ORACLE, resolve("Oracle", "jdbc:oracle:thin:@127.0.0.1:1521:xe", 21));
        assertEquals(Dialect.SQLSERVER, resolve("Microsoft SQL Server", "jdbc:sqlserver://127.0.0.1:1433", 16));
        assertEquals(Dialect.SQLSERVER, resolve("Microsoft SQL Server", "jdbc:sqlserver://127.0.0.1:1433", 11));
    }

    @Test
    public void testResolveDialect_original() throws SQLException {
        // MySQL 族
        assertEquals(Dialect.ORIGINAL, resolve("MySQL", "jdbc:mysql://127.0.0.1:3306/ruoyi-vue-pro", 8));
        assertEquals(Dialect.ORIGINAL, resolve("MariaDB", "jdbc:mariadb://127.0.0.1:3306/ruoyi-vue-pro", 11));
        assertEquals(Dialect.ORIGINAL, resolve("OceanBase", "jdbc:oceanbase://127.0.0.1:2881/ruoyi-vue-pro", 4));
        // 已有旧路径：H2、达梦、神通、人大金仓
        assertEquals(Dialect.ORIGINAL, resolve("H2", "jdbc:h2:mem:testdb;MODE=MYSQL", 2));
        assertEquals(Dialect.ORIGINAL, resolve("DM DBMS", "jdbc:dm://127.0.0.1:5236", 8));
        assertEquals(Dialect.ORIGINAL, resolve("OSCAR", "jdbc:oscar://127.0.0.1:2003/osrdb", 7));
        assertEquals(Dialect.ORIGINAL, resolve("KingbaseES", "jdbc:kingbase8://127.0.0.1:54321/test", 8));
        // 未验收的 PostgreSQL 变种：原生产品名
        assertEquals(Dialect.ORIGINAL, resolve("openGauss", "jdbc:opengauss://127.0.0.1:5432/test", 5));
        assertEquals(Dialect.ORIGINAL, resolve("GaussDB", "jdbc:gaussdb://127.0.0.1:8000/test", 5));
        // 未验收的 PostgreSQL 变种：产品名未登记或与 PostgreSQL 相同，按原生驱动 URL 识别
        assertEquals(Dialect.ORIGINAL, resolve("HighGo", "jdbc:highgo://127.0.0.1:5866/highgo", 6));
        assertEquals(Dialect.ORIGINAL, resolve("PostgreSQL", "jdbc:highgo://127.0.0.1:5866/highgo", 6));
        assertEquals(Dialect.ORIGINAL, resolve("PostgreSQL", "jdbc:opengauss://127.0.0.1:5432/test", 9));
        assertEquals(Dialect.ORIGINAL, resolve("PostgreSQL", "jdbc:mogdb://127.0.0.1:5432/test", 9));
        assertEquals(Dialect.ORIGINAL, resolve("PostgreSQL", "jdbc:gaussdb://127.0.0.1:8000/test", 9));
        assertEquals(Dialect.ORIGINAL, resolve("PostgreSQL", "jdbc:kingbase8://127.0.0.1:54321/test", 12));
        // 未验收的 SQL Server 版本
        assertEquals(Dialect.ORIGINAL, resolve("Microsoft SQL Server", "jdbc:sqlserver://127.0.0.1:1433", 10));
        assertEquals(Dialect.ORIGINAL, resolve("Microsoft SQL Server 2005", "jdbc:jtds:sqlserver://127.0.0.1:1433", 9));
    }

    @Test
    public void testResolveDialect_unsupported() {
        String secretUrl = "jdbc:sqlite:/tmp/test.db?user=root&password=secret";
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> resolve("SQLite", secretUrl, 3));
        assertTrue(ex.getMessage().contains("SQLite"));
        assertFalse(ex.getMessage().contains("secret"));
        assertFalse(ex.getMessage().contains("jdbc:"));
        // 产品名为 PostgreSQL，但 URL 不是 PostgreSQL 或已识别的变种
        assertThrows(IllegalStateException.class, () -> resolve("PostgreSQL", "jdbc:vastbase://127.0.0.1:5432/test", 9));
        // 产品名为空
        assertThrows(IllegalStateException.class, () -> resolve(null, "jdbc:unknown://127.0.0.1/test", 1));
    }

    @Test
    public void testGetDialect_cachedPerDataSource() {
        FakeDataSource postgresql = new FakeDataSource("PostgreSQL", "jdbc:postgresql://127.0.0.1:5432/a", 17);
        FakeDataSource mysql = new FakeDataSource("MySQL", "jdbc:mysql://127.0.0.1:3306/a", 8);

        assertEquals(Dialect.POSTGRESQL, SqlDialectUtils.getDialect(postgresql.getDataSource()));
        assertEquals(Dialect.ORIGINAL, SqlDialectUtils.getDialect(mysql.getDataSource()));
        assertEquals(Dialect.POSTGRESQL, SqlDialectUtils.getDialect(postgresql.getDataSource()));
        assertEquals(Dialect.ORIGINAL, SqlDialectUtils.getDialect(mysql.getDataSource()));
        assertEquals(1, postgresql.getConnectionCount());
        assertEquals(1, mysql.getConnectionCount());
    }

    @Test
    public void testGetDialect_failureNotCached() {
        FakeDataSource broken = new FakeDataSource("PostgreSQL", "jdbc:postgresql://127.0.0.1:5432/a", 17);
        broken.setFailOnConnection(true);
        assertThrows(IllegalStateException.class, () -> SqlDialectUtils.getDialect(broken.getDataSource()));
        assertThrows(IllegalStateException.class, () -> SqlDialectUtils.getDialect(broken.getDataSource()));
        assertEquals(2, broken.getConnectionCount());

        // 恢复后可以正常识别
        broken.setFailOnConnection(false);
        assertEquals(Dialect.POSTGRESQL, SqlDialectUtils.getDialect(broken.getDataSource()));
    }

    @Test
    public void testFormatDate_transactionConnectionDoesNotBorrowOrClose() throws SQLException {
        FakeDataSource h2 = new FakeDataSource("H2", "jdbc:h2:mem:single_connection", 2);
        Connection held = h2.getDataSource().getConnection();
        h2.setFailOnConnection(true); // 当前事务已占用唯一连接，任何额外借用都会失败
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("dataSource", h2.getDataSource());
        GenericApplicationContext applicationContext = new GenericApplicationContext(beanFactory);
        applicationContext.refresh();
        new SpringUtils().setApplicationContext(applicationContext);
        TransactionSynchronizationManager.bindResource(h2.getDataSource(), new ConnectionHolder(held));
        try {
            assertEquals("DATE_FORMAT(create_time, '%Y-%m-%d')", SqlDialectUtils.formatDate("create_time"));
            assertEquals(1, h2.getConnectionCount());
            assertEquals(0, h2.closeCount);
        } finally {
            TransactionSynchronizationManager.unbindResource(h2.getDataSource());
            held.close();
        }
    }


    // ========== 工具方法 ==========

    private static Dialect resolveJson(String productName, String url, int majorVersion) throws SQLException {
        return SqlDialectUtils.resolveJsonDialect(new FakeDataSource(productName, url, majorVersion).getMetaData());
    }

    /**
     * PostgreSQL 及其兼容数据库的 JSON 数组包含片段：参数转为 BIGINT 后拼成单元素 JSON 数组，null 参数拼成 [null]
     *
     * @param column JSON 数组列名
     * @return 期望的 SQL 片段
     */
    private static String postgreSQLJsonArrayContains(String column) {
        return "CAST(" + column + " AS jsonb) @> CAST(CONCAT('[', COALESCE(CAST(CAST({0} AS BIGINT) AS TEXT), 'null'), ']') AS jsonb)";
    }

    /**
     * 在 Java 中复现 Oracle 片段：REPLACE 空白、REGEXP_LIKE 整体格式与溢出、去空白后 INSTR 匹配 ,元素,
     *
     * @param json 列值，null 表示 SQL NULL
     * @param value 绑定参数
     * @return 是否匹配
     */
    private static boolean oracleJsonContains(String json, Long value) {
        return jsonContains(SqlDialectUtils.ORACLE_LONG_ARRAY_PATTERN, json, value);
    }

    /**
     * 在 Java 中复现 H2 片段，规则同 {@link #oracleJsonContains(String, Long)}，整体格式使用 H2 专用的正则
     */
    private static boolean h2JsonContains(String json, Long value) {
        return jsonContains(SqlDialectUtils.H2_LONG_ARRAY_PATTERN, json, value);
    }

    private static boolean jsonContains(String arrayPattern, String json, Long value) {
        if (json == null) {
            return false;
        }
        String text = json.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
        if (!Pattern.compile(arrayPattern).matcher(text).find()) {
            return false;
        }
        if (Pattern.compile(SqlDialectUtils.LONG_OVERFLOW_PATTERN).matcher(json).find()) {
            return false;
        }
        String elements = text.replace(" ", "").replace('[', ',').replace(']', ',');
        return elements.contains("," + (value == null ? "null" : value.toString()) + ",");
    }

    private static Dialect resolve(String productName, String url, int majorVersion) throws SQLException {
        return SqlDialectUtils.resolveDialect(new FakeDataSource(productName, url, majorVersion).getMetaData());
    }


    private static String normalize(String sql) {
        return sql.replaceAll("\\s+", " ").trim();
    }

    /**
     * 将 #{} 参数替换为 JDBC 占位符，模拟 MyBatis 生成的最终 SQL
     */
    private static String bind(String sql) {
        return sql.replaceAll("#\\{[^}]+}", "?");
    }

    /**
     * 按 BoundSql 的参数顺序，取得 Wrapper 中绑定的参数值
     */
    private static List<Object> getParameterValues(BoundSql boundSql, QueryWrapper<?> wrapper) {
        String prefix = "ew.paramNameValuePairs.";
        List<Object> values = new ArrayList<>();
        for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
            String property = parameterMapping.getProperty();
            assertTrue(property.startsWith(prefix), property);
            values.add(wrapper.getParamNameValuePairs().get(property.substring(prefix.length())));
        }
        return values;
    }

    private static void assertParameters(BoundSql boundSql, String... properties) {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        assertEquals(properties.length, parameterMappings.size());
        for (int i = 0; i < properties.length; i++) {
            assertEquals(properties[i], parameterMappings.get(i).getProperty());
        }
    }

    private static String causeMessages(Throwable ex) {
        StringBuilder messages = new StringBuilder();
        for (Throwable cause = ex; cause != null && cause != cause.getCause(); cause = cause.getCause()) {
            messages.append(cause).append('\n');
        }
        return messages.toString();
    }

    /**
     * 基于 JDK 动态代理的数据源，只提供识别方言所需的元数据，并记录获取连接的次数
     */
    private static class FakeDataSource implements InvocationHandler {

        private final String productName;
        private final String url;
        private final int majorVersion;
        private final DataSource dataSource;
        private int connectionCount;
        private int closeCount;
        private boolean failOnConnection;

        FakeDataSource(String productName, String url, int majorVersion) {
            this.productName = productName;
            this.url = url;
            this.majorVersion = majorVersion;
            this.dataSource = proxy(DataSource.class);
        }

        DataSource getDataSource() {
            return dataSource;
        }

        DatabaseMetaData getMetaData() {
            return proxy(DatabaseMetaData.class);
        }

        int getConnectionCount() {
            return connectionCount;
        }

        void setFailOnConnection(boolean failOnConnection) {
            this.failOnConnection = failOnConnection;
        }

        @SuppressWarnings("unchecked")
        private <T> T proxy(Class<T> type) {
            return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{type}, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "equals":
                    return proxy == args[0];
                case "toString":
                    return "FakeDataSource(" + productName + ")";
                case "getConnection":
                    connectionCount++;
                    if (failOnConnection) {
                        throw new SQLException("connection refused");
                    }
                    return proxy(Connection.class);
                case "getMetaData":
                    return proxy(DatabaseMetaData.class);
                case "close":
                    closeCount++;
                    return null;
                case "getDatabaseProductName":
                    return productName;
                case "getURL":
                    return url;
                case "getDatabaseMajorVersion":
                    return majorVersion;
                default:
                    throw new UnsupportedOperationException(method.getName());
            }
        }

    }

}
