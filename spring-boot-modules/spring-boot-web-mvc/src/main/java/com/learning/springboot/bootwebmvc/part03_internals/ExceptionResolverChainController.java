package com.learning.springboot.bootwebmvc.part03_internals;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advanced/exceptions")
public class ExceptionResolverChainController {

    /**
     * 触发 binder + validation 分支：
     * - 入参作为 @ModelAttribute 绑定
     * - 绑定后执行 validation
     * - 若无 Errors/BindingResult 参数承接错误，将抛出 BindException
     */
    @GetMapping(value = "/model", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> model(@Valid ExceptionQuery query) {
        return Map.of("name", query.getName());
    }

    /**
     * 触发 converter + validation 分支：
     * - JSON 解析失败：HttpMessageNotReadableException（400）
     * - JSON 合法但校验失败：MethodArgumentNotValidException（400）
     */
    @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> validate(@Valid @RequestBody ExceptionBody body) {
        return Map.of("name", body.getName());
    }

    public static class ExceptionQuery {

        @NotBlank
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ExceptionBody {

        @NotBlank
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

