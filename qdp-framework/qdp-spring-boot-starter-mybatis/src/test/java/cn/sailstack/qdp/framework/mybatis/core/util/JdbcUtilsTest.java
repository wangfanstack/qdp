package cn.sailstack.qdp.framework.mybatis.core.util;

import cn.sailstack.qdp.framework.common.util.spring.SpringUtils;
import com.baomidou.mybatisplus.annotation.DbType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link JdbcUtils} 的单元测试
 *
 * @author 芋道源码
 */
public class JdbcUtilsTest {

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

    @Test
    public void testGetDbType_cachedPerDataSource() {
        FakeDataSource mysql = new FakeDataSource("MySQL", "jdbc:mysql://127.0.0.1:3306/a");
        FakeDataSource sqlServer = new FakeDataSource("Microsoft SQL Server", "jdbc:sqlserver://127.0.0.1:1433");

        for (int i = 0; i < 3; i++) {
            assertEquals(DbType.MYSQL, JdbcUtils.getDbType(mysql.getDataSource()));
            assertEquals(DbType.SQL_SERVER, JdbcUtils.getDbType(sqlServer.getDataSource()));
        }
        assertEquals(1, mysql.getConnectionCount());
        assertEquals(1, sqlServer.getConnectionCount());
    }


    @Test
    public void testGetDbType_transactionConnectionDoesNotBorrowOrClose() throws SQLException {
        FakeDataSource h2 = new FakeDataSource("H2", "jdbc:h2:mem:single_connection");
        mockBeanFactory("dataSource", h2.getDataSource());
        Connection held = h2.getDataSource().getConnection();
        h2.setFailOnConnection(true);
        TransactionSynchronizationManager.bindResource(h2.getDataSource(), new ConnectionHolder(held));
        try {
            assertEquals(DbType.H2, JdbcUtils.getDbType());
            assertEquals(1, h2.getConnectionCount());
            assertEquals(0, h2.closeCount);
        } finally {
            TransactionSynchronizationManager.unbindResource(h2.getDataSource());
            held.close();
        }
    }


    @Test
    public void testGetDbType_unregisteredProductName() {
        // 产品名未登记时，按明确的原生驱动 URL 识别，与 getDbType(url) 的归类一致
        assertEquals(DbType.POSTGRE_SQL, JdbcUtils.getDbType(
                new FakeDataSource("HighGo", "jdbc:highgo://127.0.0.1:5866/highgo").getDataSource()));
        assertEquals(DbType.MARIADB, JdbcUtils.getDbType(
                new FakeDataSource("MariaDB", "jdbc:mariadb://127.0.0.1:3306/a").getDataSource()));

        // 完全未识别的产品：明确异常，而不是 NPE；异常信息不包含 URL
        FakeDataSource sqlite = new FakeDataSource("SQLite", "jdbc:sqlite:/tmp/test.db?user=root&password=secret");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> JdbcUtils.getDbType(sqlite.getDataSource()));
        assertTrue(ex.getMessage().contains("SQLite"));
        assertFalse(ex.getMessage().contains("secret"));
    }

    @Test
    public void testGetDbType_blankProductName() {
        assertNull(JdbcUtils.getDbType(new FakeDataSource("", "jdbc:mysql://127.0.0.1:3306/a").getDataSource()));
    }

    @Test
    public void testGetDbType_failureNotCached() {
        FakeDataSource broken = new FakeDataSource("MySQL", "jdbc:mysql://127.0.0.1:3306/a");
        broken.setFailOnConnection(true);
        assertThrows(IllegalArgumentException.class, () -> JdbcUtils.getDbType(broken.getDataSource()));
        assertThrows(IllegalArgumentException.class, () -> JdbcUtils.getDbType(broken.getDataSource()));
        assertEquals(2, broken.getConnectionCount());

        broken.setFailOnConnection(false);
        assertEquals(DbType.MYSQL, JdbcUtils.getDbType(broken.getDataSource()));
    }

    @Test
    public void testThrowIfDataAccessExceptionInTransaction() {
        // 准备参数
        QueryTimeoutException dataAccessException = new QueryTimeoutException("redis read timeout");
        IllegalStateException otherException = new IllegalStateException("other failed");

        // 调用：非事务内，不抛出
        assertDoesNotThrow(() -> JdbcUtils.throwIfDataAccessExceptionInTransaction(dataAccessException));
        // 调用：事务内，只抛出数据访问异常
        TransactionSynchronizationManager.setActualTransactionActive(true);
        try {
            assertSame(dataAccessException, assertThrows(QueryTimeoutException.class,
                    () -> JdbcUtils.throwIfDataAccessExceptionInTransaction(dataAccessException)));
            assertDoesNotThrow(() -> JdbcUtils.throwIfDataAccessExceptionInTransaction(otherException));
            assertDoesNotThrow(() -> JdbcUtils.throwIfDataAccessExceptionInTransaction(null));
        } finally {
            TransactionSynchronizationManager.setActualTransactionActive(false);
        }
    }

    private static void mockBeanFactory(String beanName, Object bean) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton(beanName, bean);
        GenericApplicationContext applicationContext = new GenericApplicationContext(beanFactory);
        applicationContext.refresh();
        new SpringUtils().setApplicationContext(applicationContext);
    }

    /**
     * 基于 JDK 动态代理的数据源，只提供识别 DB 类型所需的元数据，并记录获取连接的次数
     */
    private static class FakeDataSource implements InvocationHandler {

        private final String productName;
        private final String url;
        private final DataSource dataSource;
        private int connectionCount;
        private int closeCount;
        private boolean failOnConnection;

        FakeDataSource(String productName, String url) {
            this.productName = productName;
            this.url = url;
            this.dataSource = proxy(DataSource.class);
        }

        DataSource getDataSource() {
            return dataSource;
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
                default:
                    throw new UnsupportedOperationException(method.getName());
            }
        }

    }

}
