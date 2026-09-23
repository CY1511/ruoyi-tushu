# -*- coding: utf-8 -*-
"""前后端接口对账 v2：
   - 后端扫全部 ruoyi-* 模块（框架接口在 quartz / generator 里）
   - 前端处理 `url: '/app/goods/' + id` 这类字符串拼接
   - 用「前缀匹配 + 变量段通配」判定，避免 /{goodsId} 之类的误报
目标：只暴露「后端根本没有」的真缺口。
"""
import glob
import io
import os
import re  # noqa

BASE = os.environ.get("PROJECT_DIR", os.getcwd())
os.chdir(BASE)


def front_urls(root):
    urls = set()
    files = glob.glob(os.path.join(root, "src", "**", "*.js"), recursive=True)
    for f in files:
        s = io.open(f, encoding="utf-8", errors="ignore").read()
        for m in re.finditer(r"url:\s*([^\n]+)", s):
            expr = m.group(1)
            sm = re.search(r"[`'\"]([^`'\"]*)[`'\"]", expr)
            if not sm:
                continue
            u = sm.group(1)
            if not u.startswith("/"):
                u = "/" + u
            # 后面还有拼接（+ 变量）→ 补一个变量段
            if re.search(r"[`'\"]\s*\+", expr):
                u = u.rstrip("/") + "/{}"
            u = re.sub(r"\$\{[^}]+\}", "{}", u)
            urls.add(u)
    return urls, len(files)


def back_routes():
    routes = set()
    for mod in glob.glob(r"ruoyi-*"):
        root = os.path.join(mod, "src", "main", "java")
        if not os.path.isdir(root):
            continue
        for f in glob.glob(os.path.join(root, "**", "*.java"), recursive=True):
            s = io.open(f, encoding="utf-8", errors="ignore").read()
            if "@RestController" not in s and "@Controller" not in s:
                continue
            base_m = re.search(r'@RequestMapping\(\s*(?:value\s*=\s*)?(?:\{\s*)?"([^"]*)"', s)
            base = base_m.group(1) if base_m else ""
            for m in re.finditer(r'@(?:Get|Post|Put|Delete|Request)Mapping\(\s*(?:value\s*=\s*)?(?:\{\s*)?"([^"]*)"', s):
                full = "/" + (base.strip("/") + "/" + m.group(1).strip("/")).strip("/")
                routes.add(full)
    return routes


def segs(u):
    return [x for x in u.split("?")[0].split("/") if x]


def is_var(seg):
    return seg.startswith("{") or seg.isdigit() or seg == "{id}"


back = [segs(u) for u in back_routes()]


def matches(u):
    """前端路径的每一段都要能在某个后端路由的对应位置找到（变量段通配）"""
    f = segs(u)
    for b in back:
        if len(f) > len(b):
            continue
        ok = True
        for i, seg in enumerate(f):
            if is_var(seg) or is_var(b[i]):
                continue
            if seg.lower() != b[i].lower():
                ok = False
                break
        if ok:
            return True
    return False


def check(name, root):
    urls, nfiles = front_urls(root)
    miss = sorted(u for u in urls if not matches(u))
    print("== %s（扫描 %d 个 js）接口 %d 个，后端找不到 %d 个" % (name, nfiles, len(urls), len(miss)))
    for u in miss:
        print("     缺:", u)
    print()
    return miss


m1 = check("C 端 ruoyi-app", "ruoyi-app")
m2 = check("管理端 ruoyi-ui", "ruoyi-ui")
print("后端路由总数:", len(back))
print("总计对不上:", len(m1) + len(m2))
