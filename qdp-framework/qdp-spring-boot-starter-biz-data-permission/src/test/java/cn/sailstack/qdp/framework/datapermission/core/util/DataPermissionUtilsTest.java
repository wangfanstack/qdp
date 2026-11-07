package cn.sailstack.qdp.framework.datapermission.core.util;

import cn.sailstack.qdp.framework.datapermission.core.aop.DataPermissionContextHolder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataPermissionUtilsTest {

    @Test
    public void testExecuteIgnore() {
        DataPermissionUtils.executeIgnore(() -> assertFalse(DataPermissionContextHolder.get().enable()));
    }

}
