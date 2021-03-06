package com.hch.controller;

import com.hch.pojo.ErrorEnum;
import com.hch.pojo.KafkaMsg;
import com.hch.pojo.response.CommonResponse;
import com.hch.config.CustomProperties;
import com.hch.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@Validated
// @RestController
public class HelloController {
    @Autowired // 通过@Autowired来获取配置文件的配置bean
    private CustomProperties customProperties;
    @Autowired
    private HelloService helloService;

    @Value("${custom.author.name}")  // 通过@Value来获取配置文件的配置
    private String name;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiIgnore
    public String forwardSwagger() {
        return "redirect:/swagger-ui.html";
    }

    @GetMapping(value = "/hello")
    @ResponseBody  // 该注解表示方法所返回的对象即为返回体，而不是视图名
    public CommonResponse<String> hello(@RequestParam(value = "name", required = false) String name) {
        name = Optional.ofNullable(name).orElse(this.name);
        // log.info("get hello request, name: " + name);
        CommonResponse<String> response = new CommonResponse<>(ErrorEnum.SUCCESS);
        response.setData("hello, " + name);
        return response;
    }

    @PostMapping(value = "/err")
    // @ResponseBody    //这里就算不加这个注解也没事，因为会跳转到ExceptionAspect中返回错误信息
    public CommonResponse<String> error(@RequestBody Map<String, Object> request) {
        if (!request.containsKey("name")) {
            throw new IllegalArgumentException("missing argument 'name'");
        }
        throw new RuntimeException("argument 'name' found, but this is a problematic api :(");
    }

    @RequestMapping(value = "/author", method = RequestMethod.GET, produces = "application/JSON")
    @ResponseBody
    public CustomProperties.Person getPerson(HttpServletRequest request) {
        for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements(); ) {
            String headerName = headerNames.nextElement();
            System.out.printf("%-20s:%20s\n", headerName, request.getHeader(headerName));
        }
        return customProperties.getAuthor();
    }

    @GetMapping("/sleep")
    @ResponseBody
    public CommonResponse<String> sleep() throws InterruptedException {
        log.info("sleep begin");
        TimeUnit.MILLISECONDS.sleep(300);
        log.info("sleep end");
        return new CommonResponse<>();
    }

    @GetMapping(value = "/validation")
    @ResponseBody
    public CommonResponse<KafkaMsg> validation(@Valid KafkaMsg msg,@Min(5) Integer num) {
        log.debug("{} {}", msg, num);
        return new CommonResponse<KafkaMsg>().setData(msg);
    }

    @PostMapping("/validation")
    @ResponseBody
    public CommonResponse<KafkaMsg> validation1(@RequestBody KafkaMsg msg) {
        log.debug("{}", msg);
        // 通过service验证
        helloService.valid(msg);
        return new CommonResponse<KafkaMsg>().setData(msg);
    }
}
