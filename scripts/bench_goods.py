# -*- coding: utf-8 -*-
"""
商品详情接口压测：对比「Redis 缓存开」与「缓存关」的 QPS / 延迟。

为什么用多进程 + 多线程：单进程 Python 受 GIL 限制，可能客户端先到瓶颈，
把服务端的差距掩盖掉。这里默认 8 进程 × 8 线程 = 64 并发。

用法:
  python bench_goods.py --port 8081 --path /app/goods/100 --concurrency 64 --duration 10 --label 缓存开
"""
import argparse
import http.client
import multiprocessing as mp
import statistics
import threading
import time


def _thread_worker(host, port, path, duration, slot, stop_at):
    """单个线程：用 keep-alive 连接尽可能多地打请求，记录每次耗时"""
    lat = []
    errors = 0
    conn = http.client.HTTPConnection(host, port, timeout=15)
    while time.perf_counter() < stop_at:
        t0 = time.perf_counter()
        try:
            conn.request("GET", path)
            resp = conn.getresponse()
            resp.read()
            if resp.status != 200:
                errors += 1
        except Exception:
            errors += 1
            try:
                conn.close()
            except Exception:
                pass
            conn = http.client.HTTPConnection(host, port, timeout=15)
        lat.append(time.perf_counter() - t0)
    try:
        conn.close()
    except Exception:
        pass
    slot.append((lat, errors))


def _process_main(job):
    host, port, path, procs, threads, warmup, duration = job
    # 预热：让 JIT / 连接池 / 缓存先热起来，预热数据不计入
    stop_warm = time.perf_counter() + warmup
    _thread_worker(host, port, path, warmup, [], stop_warm)

    per = threads // procs
    results = []
    stop_at = time.perf_counter() + duration
    ts = [threading.Thread(target=_thread_worker,
                           args=(host, port, path, duration, results, stop_at))
          for _ in range(per)]
    for t in ts:
        t.start()
    for t in ts:
        t.join()

    latents = []
    errors = 0
    for lat, err in results:
        latents.extend(lat)
        errors += err
    return latents, errors


def bench(host, port, path, concurrency, duration, procs=8, warmup=2):
    procs = max(1, min(procs, concurrency))
    job = (host, port, path, procs, concurrency, warmup, duration)
    t0 = time.perf_counter()
    with mp.Pool(processes=procs) as pool:
        out = pool.map(_process_main, [job] * procs)
    wall = time.perf_counter() - t0 - warmup

    latents = []
    errors = 0
    for lat, err in out:
        latents.extend(lat)
        errors += err
    latents.sort()

    def pct(p):
        if not latents:
            return 0.0
        i = min(len(latents) - 1, int(len(latents) * p))
        return latents[i] * 1000

    return {
        "wall": wall,
        "count": len(latents),
        "qps": len(latents) / wall if wall else 0,
        "avg_ms": (sum(latents) / len(latents) * 1000) if latents else 0,
        "p50_ms": pct(0.50),
        "p95_ms": pct(0.95),
        "p99_ms": pct(0.99),
        "max_ms": (latents[-1] * 1000) if latents else 0,
        "errors": errors,
    }


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--host", default="127.0.0.1")
    ap.add_argument("--port", type=int, default=8081)
    ap.add_argument("--path", default="/app/goods/100")
    ap.add_argument("--concurrency", type=int, default=64)
    ap.add_argument("--duration", type=int, default=10)
    ap.add_argument("--procs", type=int, default=8)
    ap.add_argument("--label", default="")
    a = ap.parse_args()

    print("压测 %s%s  并发=%d  持续=%ds" % (a.host, a.path, a.concurrency, a.duration))
    r = bench(a.host, a.port, a.path, a.concurrency, a.duration, a.procs)
    print("  标签       : %s" % (a.label or "-"))
    print("  总请求数   : %d" % r["count"])
    print("  实际耗时   : %.2f s" % r["wall"])
    print("  QPS        : %.0f" % r["qps"])
    print("  平均延迟   : %.2f ms" % r["avg_ms"])
    print("  P50 / P95  : %.2f / %.2f ms" % (r["p50_ms"], r["p95_ms"]))
    print("  P99 / Max  : %.2f / %.2f ms" % (r["p99_ms"], r["max_ms"]))
    print("  失败请求   : %d" % r["errors"])
    print()
    print("RESULT %s" % r)


if __name__ == "__main__":
    mp.freeze_support()
    main()
