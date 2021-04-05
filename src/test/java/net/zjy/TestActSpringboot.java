package net.zjy;

import lombok.extern.slf4j.Slf4j;
import net.zjy.config.DemoApplicationConfig;
import net.zjy.utils.SecurityUtil;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.model.payloads.StartProcessPayload;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.RuntimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;

/**
 * @description: act整合springboot 测试类
 * Activiti7可以   自动部署流程  ，前提是在resources目录下，创建一个新的目录processes，用来放置bpmn文件。
 * 创建一个简单的Bpmn流程文件，并设置任务的用户组Candidate Groups
 * <p>
 * Candidate Groups中的内容与上面DemoApplicationConfiguration类中出现的用户组名称要保持一致，可以填写：
 * activitiTeam 或者 otherTeam
 * @author:@zhujinyu
 * @date:2021-04-05 20:30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestActSpringboot {

    private Logger logger = LoggerFactory.getLogger(DemoApplicationConfig.class);

    @Resource
    private ProcessRuntime processRuntime;

    @Resource
    private TaskRuntime taskRuntime;

    @Resource
    private SecurityUtil securityUtil;

    //查询流程定义
    @Test
    public void testProcess() {
        //注入用户
        securityUtil.logInAs("jack");
        //流程定义分页查询
        Page<ProcessDefinition> definitionPage = processRuntime.processDefinitions(Pageable.of(0, 10));
        logger.info("流程定义总数： 【{}】", definitionPage.getTotalItems());
        for (ProcessDefinition processDefinition : definitionPage.getContent()) {
            System.out.println("============");
            logger.info("流程定义数量:【{}】", processDefinition);
            System.out.println("============");
        }

    }


    //启动流程
    @Test
    public void startProcess() {
        securityUtil.logInAs("system");
        // activti7 特有的启动方式
        ProcessInstance instance = processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey("actDemo")
                .build());

        logger.info("启动后获取流程实例:【{}】", instance);


    }

    //执行任务
    @Test
    public void testExecuteTask() {
        //设置登录用户
        securityUtil.logInAs("tom");
        //查询任务
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));
        if (tasks != null) {
            logger.info("当前用户任务数：【{}】", tasks.getTotalItems());
            if (tasks.getContent().size() > 0) {
                for (Task task : tasks.getContent()) {
                    //拾取任务
                    Task taskClaim = taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
                    logger.info("拾取到任务并完成：【{}】", taskClaim);
                    //完成任务
                    taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
                }
            }
        }





    }


}
