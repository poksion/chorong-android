package net.poksion.chorong.android.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ResourceStringTest {

    private Context mockContext;

    private static final int ID_FOR_IDX0 = 0;
    private static final String DUMMY_IDX_0_STR = "dummy-idx-0-str";

    private static final int ID_FOR_IDX1 = 1;
    private static final String DUMMY_IDX_1_STR = "dummy-idx-1-str : ";

    private static final int ID_FOR_IDX2 = 2;
    private static final String DUMMY_IDX_2_STR = "dummy-idx-2-str : ";

    private static final int ID_FOR_IDX3 = 3;
    private static final String DUMMY_IDX_3_STR = "dummy-idx-3-str : ";

    @SuppressWarnings("ResourceType")
    @Before
    public void setUp() {
        mockContext = mock(Context.class);
        when(mockContext.getString(ID_FOR_IDX0)).thenReturn(DUMMY_IDX_0_STR);
        when(mockContext.getString(eq(ID_FOR_IDX1), Matchers.any())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return DUMMY_IDX_1_STR + args[1];
            }
        });
        when(mockContext.getString(eq(ID_FOR_IDX2), Matchers.any(), Matchers.any())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return DUMMY_IDX_2_STR + args[1] + "," + args[2];
            }
        });
        when(mockContext.getString(eq(ID_FOR_IDX3), Matchers.any(), Matchers.any(), Matchers.any())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return DUMMY_IDX_3_STR + args[1] + "," + args[2] + "," + args[3];
            }
        });

    }

    @Test
    public void index_should_work_same_way_on_java() {
        Resource.String0 string0 = new ResourceImpl.String0(mockContext);
        assertThat(string0.read(ID_FOR_IDX0)).isEqualTo(DUMMY_IDX_0_STR);

        Resource.String1<Integer> string1 = new ResourceImpl.String1<>(mockContext);
        assertThat(string1.read(ID_FOR_IDX1, 1)).isEqualTo("dummy-idx-1-str : 1");

        Resource.String2<Integer, String> string2 = new ResourceImpl.String2<>(mockContext);
        assertThat(string2.read(ID_FOR_IDX2, 1, "two")).isEqualTo("dummy-idx-2-str : 1,two");

        Resource.String3<Integer, String, Object> string3 = new ResourceImpl.String3<>(mockContext);
        assertThat(string3.read(ID_FOR_IDX3, 1, "two", new Object(){ public String toString(){ return "THREE"; } })).isEqualTo("dummy-idx-3-str : 1,two,THREE");
    }

}
