package cn.sailstack.qdp.framework.common.util.monitor;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link TracerUtils} 的单元测试
 *
 * @author sailstack源码
 */
public class TracerUtilsTest {

    @Test
    public void testGetTraceId_whenSpanPresent() {
        String traceId = "0af7651916cd43dd8448eb211c80319c";
        SpanContext spanContext = SpanContext.create(traceId, "5777111122223333",
                TraceFlags.getSampled(), TraceState.getDefault());
        try (var ignored = Span.wrap(spanContext).makeCurrent()) {
            assertEquals(traceId, TracerUtils.getTraceId());
        }
    }

    @Test
    public void testGetTraceId_whenSpanAbsent_shouldReturnEmptyString() {
        assertEquals("", TracerUtils.getTraceId());
    }

}
