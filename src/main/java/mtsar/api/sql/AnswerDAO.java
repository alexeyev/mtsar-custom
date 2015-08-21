package mtsar.api.sql;

import mtsar.api.Answer;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Iterator;
import java.util.List;

@RegisterMapper(AnswerMapper.class)
public interface AnswerDAO {
   String DEFAULT_ANSWER_TYPE = "answer";

    @SqlQuery("select * from answers where process = :process")
    List<Answer> listForProcess(@Bind("process") String process);

    @SqlQuery("select * from answers where task_id = :taskId and process = :process")
    List<Answer> listForTask(@Bind("taskId") Integer taskId, @Bind("process") String process);

    @SqlQuery("select * from answers where worker_id = :workerId and process = :process")
    List<Answer> listForWorker(@Bind("workerId") Integer workerId, @Bind("process") String process);

    @SqlQuery("select * from answers where id = :id and process = :process limit 1")
    Answer find(@Bind("id") Integer id, @Bind("process") String process);

    @SqlQuery("insert into answers (process, datetime, tags, type, worker_id, task_id, answers) values (:process, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[]), cast(:type as answer_type), :workerId, :taskId, cast(:answersTextArray as text[])) returning id")
    int insert(@BindBean Answer a);

    @SqlBatch("insert into answers (id, process, datetime, tags, type, worker_id, task_id, answers) values (coalesce(:id, nextval('answers_id_seq')), :process, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[]), cast(:type as answer_type), :workerId, :taskId, cast(:answersTextArray as text[]))")
    @BatchChunkSize(1000)
    void insert(@BindBean Iterator<Answer> tasks);

    @SqlQuery("select count(*) from answers")
    int count();

    @SqlQuery("select count(*) from answers where process = :process")
    int count(@Bind("process") String process);

    @SqlUpdate("delete from answers where id = :id and process = :process")
    void delete(@Bind("id") Integer id, @Bind("process") String process);

    @SqlUpdate("delete from answers where process = :process")
    void deleteAll(@Bind("process") String process);

    @SqlUpdate("delete from answers")
    void deleteAll();

    @SqlUpdate("select setval('answers_id_seq', coalesce((select max(id) + 1 from answers), 1), false)")
    void resetSequence();

    void close();
}
