package mtsar.task;

import com.google.common.collect.Lists;
import mtsar.api.*;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.processors.task.InverseCountAllocator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class InverseCountAllocatorTest {
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final AnswerDAO answerDAO = mock(AnswerDAO.class);
    private static final Process process = mock(Process.class);
    private static final Worker worker = mock(Worker.class);
    private static final List<Task> tasks = Lists.newArrayList(mock(Task.class), mock(Task.class));
    private static final List<Answer> answers1 = Lists.newArrayList(mock(Answer.class));
    private static final List<Answer> answers2 = Lists.newArrayList(mock(Answer.class), mock(Answer.class));
    private static final InverseCountAllocator allocator = new InverseCountAllocator(Process.wrap(process), taskDAO, answerDAO);

    @Before
    public void setup() {
        when(process.getId()).thenReturn("1");
        when(worker.getId()).thenReturn(1);
        when(tasks.get(0).getId()).thenReturn(1);
        when(tasks.get(1).getId()).thenReturn(2);
    }

    @Test
    public void testUnequalAllocation() {
        reset(taskDAO);
        reset(answerDAO);
        when(taskDAO.remaining(anyString(), eq(1))).thenReturn(1);
        when(taskDAO.listForProcess(anyString())).thenReturn(tasks);
        when(answerDAO.listForTask(eq(1), anyString())).thenReturn(answers1);
        when(answerDAO.listForTask(eq(2), anyString())).thenReturn(answers2);
        final Optional<TaskAllocation> allocation = allocator.allocate(worker);
        assertThat(allocation.isPresent()).isTrue();
        assertThat(allocation.get().getTask()).isEqualTo(tasks.get(0));
    }

    @Test
    public void testEqualAllocation() {
        reset(taskDAO);
        reset(answerDAO);
        when(taskDAO.remaining(anyString(), eq(1))).thenReturn(1);
        when(taskDAO.listForProcess(anyString())).thenReturn(tasks);
        when(answerDAO.listForTask(eq(1), anyString())).thenReturn(answers1);
        when(answerDAO.listForTask(eq(2), anyString())).thenReturn(answers1);
        final Optional<TaskAllocation> allocation = allocator.allocate(worker);
        assertThat(allocation.isPresent()).isTrue();
        assertThat(allocation.get().getTask()).isIn(tasks);
    }

    @Test
    public void testNothingLeft() {
        reset(taskDAO);
        reset(answerDAO);
        final Optional<TaskAllocation> allocation = allocator.allocate(worker);
        assertThat(allocation.isPresent()).isFalse();
    }

    @Test
    public void testEmpty() {
        reset(taskDAO);
        reset(answerDAO);
        when(taskDAO.remaining(anyString(), eq(1))).thenReturn(1);
        final Optional<TaskAllocation> allocation = allocator.allocate(worker);
        assertThat(allocation.isPresent()).isFalse();
    }
}
