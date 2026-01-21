#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
为章节 upsert “章节学习卡片（五问闭环）”。

目标：
- 覆盖模块 docs 章节（SSOT：docs/README.md 链接清单）
- 覆盖 Book-only 页面（docs/book/**/*.md）
- 可重复执行（idempotent）：多次运行不会重复叠加

卡片字段（五问）：
- 知识点：本章讲什么
- 怎么使用：落地方式/最小步骤
- 原理：主线 + 关键分支
- 源码入口：关键类/关键方法/断点锚点
- 推荐 Lab：至少 1 个可跑 Lab/Test

用法：
  python3 scripts/upsert-chapter-cards.py
  python3 scripts/upsert-chapter-cards.py --dry-run
  python3 scripts/upsert-chapter-cards.py --modules-only
  python3 scripts/upsert-chapter-cards.py --module springboot-web-mvc
"""

from __future__ import annotations

import argparse
import os
import re
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

from repo_paths import find_module_root


MD_LINK_WITH_TEXT_RE = re.compile(r"(!?)\[([^\]]*)\]\(([^)]+)\)")
SCHEME_RE = re.compile(r"^[a-zA-Z][a-zA-Z0-9+.-]*:")

CHAPTER_CARD_START = "<!-- CHAPTER-CARD:START -->"
CHAPTER_CARD_END = "<!-- CHAPTER-CARD:END -->"

LAB_INLINE_RE = re.compile(r"Lab[:：]\s*`?([A-Za-z_][A-Za-z0-9_]*LabTest)(?:#([A-Za-z0-9_]+))?`?")
LAB_REF_RE = re.compile(r"\b([A-Za-z_][A-Za-z0-9_]*LabTest)(?:#([A-Za-z0-9_]+))?\b")


@dataclass(frozen=True)
class ModuleMeta:
    how_to_use: str
    principle: str
    source_entry: list[str]


MODULE_META: dict[str, ModuleMeta] = {
    "springboot-basics": ModuleMeta(
        how_to_use="通过 `application.yml`/环境变量/命令行等配置进入 `Environment`，用 `@ConfigurationProperties` 做类型安全绑定，并将最终值用于条件装配或业务逻辑。",
        principle="配置源（PropertySource）→ `Environment` 聚合与覆盖 → Binder 绑定 → Profile/优先级分流 → 影响条件装配与运行期行为。",
        source_entry=[
            "`org.springframework.core.env.ConfigurableEnvironment`",
            "`org.springframework.core.env.PropertySource`",
            "`org.springframework.boot.context.properties.bind.Binder`",
            "`org.springframework.boot.context.properties.ConfigurationPropertiesBinder`",
        ],
    ),
    "springboot-web-mvc": ModuleMeta(
        how_to_use="编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。",
        principle="HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。",
        source_entry=[
            "`org.springframework.web.servlet.DispatcherServlet#doDispatch`",
            "`org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping`",
            "`org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod`",
            "`org.springframework.web.servlet.HandlerExceptionResolver`",
        ],
    ),
    "springboot-data-jpa": ModuleMeta(
        how_to_use="通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。",
        principle="Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。",
        source_entry=[
            "`org.springframework.data.jpa.repository.support.SimpleJpaRepository`",
            "`org.springframework.data.jpa.repository.support.JpaRepositoryFactory`",
            "`jakarta.persistence.EntityManager`",
            "`org.springframework.orm.jpa.JpaTransactionManager`",
        ],
    ),
    "springboot-cache": ModuleMeta(
        how_to_use="在方法边界使用 `@Cacheable/@CachePut/@CacheEvict` 声明缓存意图；根据 key/condition/unless 设计缓存命中与一致性策略。",
        principle="方法调用 → AOP 代理 → CacheInterceptor → key 计算（KeyGenerator/SpEL）→ 命中短路/不命中回源 → 回写/失效。",
        source_entry=[
            "`org.springframework.cache.interceptor.CacheInterceptor`",
            "`org.springframework.cache.interceptor.CacheAspectSupport`",
            "`org.springframework.cache.interceptor.KeyGenerator`",
            "`org.springframework.cache.CacheManager`",
        ],
    ),
    "springboot-async-scheduling": ModuleMeta(
        how_to_use="用 `@Async` 把执行切到线程池（TaskExecutor），用 `@Scheduled` 让任务按 cron/fixedDelay/fixedRate 触发；明确线程池配置与异常可见性。",
        principle="方法调用 → 代理拦截（Async/Scheduling）→ 提交到 Executor/Scheduler → 线程池执行 → 返回值/异常传播语义决定可观察性与稳定性。",
        source_entry=[
            "`org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor`",
            "`org.springframework.aop.interceptor.AsyncExecutionInterceptor`",
            "`org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor`",
            "`org.springframework.core.task.TaskExecutor`",
        ],
    ),
    "springboot-actuator": ModuleMeta(
        how_to_use="通过 Actuator endpoints 暴露健康检查/信息/指标；用 exposure 控制可见范围，并在生产环境结合鉴权与安全边界。",
        principle="引入 Actuator → 端点注册与 discover → exposure 决定暴露 → Web 层映射为 HTTP 端点 → 结合安全策略与可观测信号使用。",
        source_entry=[
            "`org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration`",
            "`org.springframework.boot.actuate.endpoint.annotation.Endpoint`",
            "`org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties`",
        ],
    ),
    "springboot-testing": ModuleMeta(
        how_to_use="按目标选择测试切片（如 `@WebMvcTest`）或全量上下文（`@SpringBootTest`）；用 mock/替身把外部依赖固定成可断言证据。",
        principle="测试注解决定上下文装配范围 → TestContext 缓存与复用 → slice/full context 的权衡 → 断言固化机制结论 → 快速定位失败。",
        source_entry=[
            "`org.springframework.boot.test.context.SpringBootTest`",
            "`org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest`",
            "`org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate`",
        ],
    ),
    "springboot-security": ModuleMeta(
        how_to_use="将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。",
        principle="HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。",
        source_entry=[
            "`org.springframework.security.web.FilterChainProxy`",
            "`org.springframework.security.web.SecurityFilterChain`",
            "`org.springframework.security.web.access.intercept.AuthorizationFilter`",
        ],
    ),
    "springboot-web-client": ModuleMeta(
        how_to_use="用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。",
        principle="构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。",
        source_entry=[
            "`org.springframework.web.reactive.function.client.WebClient`",
            "`org.springframework.web.reactive.function.client.ExchangeFilterFunction`",
            "`org.springframework.web.reactive.function.client.ExchangeFunction`",
        ],
    ),
    "springboot-business-case": ModuleMeta(
        how_to_use="用端到端链路把 Web/Validation/Security/AOP/Tx/JPA/Events 串起来：遇到红测/异常时，先定位“哪个边界没生效”，再回到对应模块主线。",
        principle="一次业务请求贯穿：MVC 入参→安全边界→事务边界→持久化上下文→事件时机→可观测信号；排障的关键是把问题归类到具体边界。",
        source_entry=[
            "`org.springframework.web.servlet.DispatcherServlet#doDispatch`",
            "`org.springframework.transaction.interceptor.TransactionInterceptor#invoke`",
            "`org.springframework.data.jpa.repository.support.SimpleJpaRepository`",
        ],
    ),
    "spring-core-beans": ModuleMeta(
        how_to_use="通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。",
        principle="`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。",
        source_entry=[
            "`org.springframework.context.support.AbstractApplicationContext#refresh`",
            "`org.springframework.beans.factory.support.DefaultListableBeanFactory`",
            "`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`",
            "`org.springframework.context.support.PostProcessorRegistrationDelegate`",
        ],
    ),
    "spring-core-aop": ModuleMeta(
        how_to_use="通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。",
        principle="目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。",
        source_entry=[
            "`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization`",
            "`org.springframework.aop.framework.ProxyFactory`",
            "`org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`",
        ],
    ),
    "spring-core-aop-weaving": ModuleMeta(
        how_to_use="当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。",
        principle="代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。",
        source_entry=[
            "`org.springframework.context.weaving.AspectJWeavingEnabler`",
            "`org.springframework.instrument.classloading.LoadTimeWeaver`",
            "`org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter`",
        ],
    ),
    "spring-core-events": ModuleMeta(
        how_to_use="通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。",
        principle="publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。",
        source_entry=[
            "`org.springframework.context.event.SimpleApplicationEventMulticaster`",
            "`org.springframework.context.event.ApplicationListenerMethodAdapter`",
            "`org.springframework.transaction.support.TransactionSynchronizationManager`",
        ],
    ),
    "spring-core-validation": ModuleMeta(
        how_to_use="在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。",
        principle="约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。",
        source_entry=[
            "`org.springframework.validation.beanvalidation.LocalValidatorFactoryBean`",
            "`org.springframework.validation.beanvalidation.MethodValidationPostProcessor`",
            "`org.springframework.validation.beanvalidation.SpringValidatorAdapter`",
        ],
    ),
    "spring-core-resources": ModuleMeta(
        how_to_use="通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。",
        principle="定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。",
        source_entry=[
            "`org.springframework.core.io.Resource`",
            "`org.springframework.core.io.ResourceLoader`",
            "`org.springframework.core.io.support.PathMatchingResourcePatternResolver`",
        ],
    ),
    "spring-core-tx": ModuleMeta(
        how_to_use="在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。",
        principle="方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。",
        source_entry=[
            "`org.springframework.transaction.interceptor.TransactionInterceptor#invoke`",
            "`org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction`",
            "`org.springframework.transaction.PlatformTransactionManager`",
        ],
    ),
    "spring-core-profiles": ModuleMeta(
        how_to_use="用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。",
        principle="激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。",
        source_entry=[
            "`org.springframework.context.annotation.Profile`",
            "`org.springframework.context.annotation.ConditionEvaluator#shouldSkip`",
            "`org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles`",
        ],
    ),
}


DEFAULT_MODULE_META = ModuleMeta(
    how_to_use="建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。",
    principle="主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。",
    source_entry=["（以本章正文“源码/断点”小节为准）"],
)


BOOK_PAGE_TO_MODULE: dict[str, str] = {
    "00-start-here.md": "spring-core-beans",
    "01-boot-basics-mainline.md": "springboot-basics",
    "02-ioc-container-mainline.md": "spring-core-beans",
    "03-aop-proxy-mainline.md": "spring-core-aop",
    "04-aop-weaving-mainline.md": "spring-core-aop-weaving",
    "05-tx-mainline.md": "spring-core-tx",
    "06-webmvc-mainline.md": "springboot-web-mvc",
    "07-security-mainline.md": "springboot-security",
    "08-data-jpa-mainline.md": "springboot-data-jpa",
    "09-cache-mainline.md": "springboot-cache",
    "10-async-scheduling-mainline.md": "springboot-async-scheduling",
    "11-events-mainline.md": "spring-core-events",
    "12-resources-mainline.md": "spring-core-resources",
    "13-profiles-mainline.md": "spring-core-profiles",
    "14-validation-mainline.md": "spring-core-validation",
    "15-actuator-observability-mainline.md": "springboot-actuator",
    "16-web-client-mainline.md": "springboot-web-client",
    "17-testing-mainline.md": "springboot-testing",
    "18-business-case.md": "springboot-business-case",
}


def is_external_link(dest: str) -> bool:
    if dest.startswith("#"):
        return True
    if dest.startswith("//"):
        return True
    return bool(SCHEME_RE.match(dest))


def normalize_md_link_target(raw: str) -> str | None:
    dest = raw.strip()
    if not dest:
        return None
    if dest.startswith("<") and dest.endswith(">"):
        dest = dest[1:-1].strip()
    if " " in dest or "\t" in dest:
        dest = re.split(r"\s+", dest, maxsplit=1)[0]
    if "#" in dest:
        dest = dest.split("#", 1)[0]
    return dest or None


def discover_modules(repo_root: Path) -> list[str]:
    """
    发现“有模块目录页”的模块：以 docs/<topic>/<module>/README.md 为准，并要求代码模块目录存在。
    """
    docs_root = repo_root / "docs"
    if not docs_root.is_dir():
        return []

    modules: list[str] = []
    for readme in sorted(docs_root.glob("*/*/README.md")):
        module = readme.parent.name
        if find_module_root(repo_root, module) is None:
            continue
        modules.append(module)
    return modules


def resolve_docs_readme(repo_root: Path, module: str) -> Path | None:
    candidates = sorted((repo_root / "docs").glob(f"*/{module}/README.md"))
    return candidates[0] if candidates else None


def iter_links_from_docs_readme(readme: Path) -> Iterable[str]:
    content = readme.read_text(encoding="utf-8", errors="replace")
    for m in MD_LINK_WITH_TEXT_RE.finditer(content):
        is_image = m.group(1) == "!"
        if is_image:
            continue
        yield m.group(3)


def iter_module_chapters(repo_root: Path, docs_readme: Path) -> list[Path]:
    if not docs_readme.is_file():
        return []

    index = 0
    last: dict[Path, int] = {}
    for target_raw in iter_links_from_docs_readme(docs_readme):
        index += 1
        target = normalize_md_link_target(target_raw)
        if target is None or is_external_link(target):
            continue
        if not target.endswith(".md"):
            continue
        chapter = (docs_readme.parent / target).resolve()
        try:
            chapter.relative_to(repo_root)
        except ValueError:
            continue
        if "/docs/" not in chapter.as_posix():
            continue
        if chapter == docs_readme:
            continue
        last[chapter] = index
    return [p for (p, _) in sorted(last.items(), key=lambda it: it[1])]


def iter_book_pages(repo_root: Path) -> list[Path]:
    root = repo_root / "docs" / "book"
    if not root.is_dir():
        return []
    return [p for p in sorted(root.rglob("*.md")) if p.is_file()]


def extract_title(md_text: str) -> str | None:
    for line in md_text.splitlines():
        if line.startswith("# "):
            return line.removeprefix("# ").strip() or None
    return None


def clean_title_for_knowledge_point(title: str) -> str:
    t = title.strip()
    t = re.sub(r"^\s*第\s*\d+\s*章[:：]\s*", "", t)
    t = re.sub(r"^\s*\d+\s*[.．]\s*", "", t)
    t = re.sub(r"^\s*\d+\s*[-–]\s*", "", t)
    return t.strip()


def build_test_class_index(module_root: Path) -> set[str]:
    test_root = module_root / "src" / "test" / "java"
    if not test_root.is_dir():
        return set()
    return {p.stem for p in test_root.rglob("*.java") if p.is_file()}


def extract_first_lab(text: str, module_test_classes: set[str]) -> str | None:
    # 1) Prefer explicit “Lab：XxxLabTest(#method)” lines
    for m in LAB_INLINE_RE.finditer(text):
        cls = m.group(1)
        method = m.group(2)
        if cls not in module_test_classes:
            continue
        return f"{cls}#{method}" if method else cls

    # 2) Fallback: any LabTest token in text
    for m in LAB_REF_RE.finditer(text):
        cls = m.group(1)
        method = m.group(2)
        if not cls.endswith("LabTest"):
            continue
        if cls not in module_test_classes:
            continue
        return f"{cls}#{method}" if method else cls

    return None


def default_module_lab(repo_root: Path, module_root: Path, module_test_classes: set[str]) -> str | None:
    timeline = module_root / "docs" / "part-00-guide" / "03-mainline-timeline.md"
    if not timeline.is_file():
        return None
    text = timeline.read_text(encoding="utf-8", errors="replace")
    return extract_first_lab(text, module_test_classes)


def remove_existing_card(text: str) -> tuple[str, bool]:
    start = text.find(CHAPTER_CARD_START)
    if start < 0:
        return text, False
    end = text.find(CHAPTER_CARD_END, start)
    if end < 0:
        return text, False
    end += len(CHAPTER_CARD_END)
    new = text[:start] + text[end:]
    # clean up extra blank lines around removal
    new = re.sub(r"\n{4,}", "\n\n\n", new)
    return new, True


def format_source_entry(items: list[str]) -> str:
    # items are expected to contain inline-code already; keep it compact.
    return " / ".join(items)


def build_card_block(
    *,
    knowledge_point: str,
    how_to_use: str,
    principle: str,
    source_entry: str,
    recommended_lab: str,
) -> str:
    return (
        f"{CHAPTER_CARD_START}\n"
        f'!!! summary "章节学习卡片（五问闭环）"\n'
        f"\n"
        f"    - 知识点：{knowledge_point}\n"
        f"    - 怎么使用：{how_to_use}\n"
        f"    - 原理：{principle}\n"
        f"    - 源码入口：{source_entry}\n"
        f"    - 推荐 Lab：{recommended_lab}\n"
        f"{CHAPTER_CARD_END}\n"
    )


def insert_card_after_title(text: str, card_block: str) -> str:
    lines = text.splitlines(keepends=True)
    for i, line in enumerate(lines):
        if line.startswith("# "):
            insert_at = i + 1
            # ensure one blank line after title before card
            if insert_at < len(lines) and lines[insert_at].strip() != "":
                lines.insert(insert_at, "\n")
                insert_at += 1
            elif insert_at < len(lines) and lines[insert_at].strip() == "":
                # keep one blank line
                pass
            lines.insert(insert_at, card_block + "\n")
            return "".join(lines)
    # no title found; append at top
    return card_block + "\n" + text


def determine_book_page_meta(repo_root: Path, page: Path) -> tuple[ModuleMeta, str | None, str]:
    name = page.name
    module = BOOK_PAGE_TO_MODULE.get(name)
    if not module:
        # tool/appendix pages
        return (
            ModuleMeta(
                how_to_use="本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。",
                principle="本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。",
                source_entry=["N/A（本页为索引/工具页）"],
            ),
            None,
            "",
        )
    module_root = find_module_root(repo_root, module)
    if module_root is None:
        return MODULE_META.get(module, DEFAULT_MODULE_META), None, module
    test_classes = build_test_class_index(module_root)
    lab = default_module_lab(repo_root, module_root, test_classes) or None
    return MODULE_META.get(module, DEFAULT_MODULE_META), lab, module


def parse_args(argv: list[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        prog="upsert-chapter-cards.py",
        description="为章节 upsert 学习卡片（五问闭环）。",
    )
    parser.add_argument("--dry-run", action="store_true", help="只输出统计信息，不写文件")
    parser.add_argument("--modules-only", action="store_true", help="只处理模块 docs 章节（不处理 Book-only）")
    parser.add_argument("--book-only", action="store_true", help="只处理 Book-only（不处理模块 docs）")
    parser.add_argument("--module", action="append", default=[], help="仅处理指定模块（可重复传入）")
    return parser.parse_args(argv)


def main(argv: list[str]) -> int:
    repo_root = Path(__file__).resolve().parents[1]
    args = parse_args(argv[1:])

    handle_modules = not args.book_only
    handle_book = not args.modules_only
    module_filter = set(args.module or [])

    targets: list[tuple[str, Path]] = []

    module_test_index: dict[str, set[str]] = {}
    module_default_lab: dict[str, str | None] = {}

    if handle_modules:
        for module in discover_modules(repo_root):
            if module_filter and module not in module_filter:
                continue
            module_root = find_module_root(repo_root, module)
            if module_root is None:
                continue
            docs_readme = resolve_docs_readme(repo_root, module)
            if docs_readme is None:
                continue
            test_classes = build_test_class_index(module_root)
            module_test_index[module] = test_classes
            module_default_lab[module] = default_module_lab(repo_root, module_root, test_classes)
            for chapter in iter_module_chapters(repo_root, docs_readme):
                targets.append((module, chapter))

    if handle_book:
        for page in iter_book_pages(repo_root):
            targets.append(("", page))

    updated = 0
    skipped = 0
    failed = 0

    for module, path in targets:
        try:
            text = path.read_text(encoding="utf-8", errors="replace")
        except OSError:
            failed += 1
            continue

        title = extract_title(text)
        if not title:
            skipped += 1
            continue

        base_text, _ = remove_existing_card(text)

        if module:
            meta = MODULE_META.get(module, DEFAULT_MODULE_META)
            test_classes = module_test_index.get(module, set())
            lab = extract_first_lab(base_text, test_classes) or module_default_lab.get(module) or ""
            recommended_lab = f"`{lab}`" if lab else "N/A"
            how_to_use = f"建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：{meta.how_to_use}"
            principle = meta.principle
            source_entry = format_source_entry(meta.source_entry)
        else:
            meta, lab, book_module = determine_book_page_meta(repo_root, path)
            recommended_lab = f"`{lab}`" if lab else "N/A"
            how_to_use = meta.how_to_use
            principle = meta.principle
            source_entry = format_source_entry(meta.source_entry)

        knowledge_point = clean_title_for_knowledge_point(title)

        card = build_card_block(
            knowledge_point=knowledge_point,
            how_to_use=how_to_use,
            principle=principle,
            source_entry=source_entry,
            recommended_lab=recommended_lab,
        )

        new_text = insert_card_after_title(base_text.strip("\n") + "\n", card)

        if new_text != text:
            updated += 1
            if not args.dry_run:
                path.write_text(new_text, encoding="utf-8")

    print(f"[OK] Chapter cards upsert finished: updated={updated}, skipped={skipped}, failed={failed}")
    if args.dry_run:
        print("[INFO] dry-run mode: no files were written")
    return 0 if failed == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main(os.sys.argv))
