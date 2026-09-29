# Thesis

This page contains my business hypotheses for my mobile analytics tool, [Undercurrent Analytics](https://undercurrentanalytics.dev).

It is shorter and simpler than the Banyan thesis. It's less thought out and even more driven by my own needs rather than the needs of others. Also, I'm not fussed about exploring and vetting it in detail through the process of writing.

## The customer, their problem, and the existing landscape of solutions

I'm a backend developer, yet modern development tools allow me to bring fully fledged software to market extremely quickly. I know that product analytics are extremely important, but I dislike all the available analytics tools I've tried, because they rely heavily on click-ops visualization and query builders. I'm used to and like Grafana. Visualization definitions along with query definitions can be automatically provisioned and version controlled. Furthermore, LLMs are very good at building them even without specialized vendor tools - this is because Grafana is open source. Another reason why I don't like click-ops query builders is because it means I can't define queries using SQL - I have to use a proprietary vendor-based query language (if any query language is exposed at all, which is not always the case). There are some vendors which allow you to query data using SQL, but they're all very expensive compared to the price of running the service yourself (e.g. tinybird, which costs a whopping $49/month for data ingest and storage: And this is not even unlimited).

## Product

Undercurrent Analytics provides a self-serve platform for mobile analytics, providing event ingest, storage, queries, and visualization, at a competitive cost.

We reuse the existing open source analytics format from Mixpanel, our platform is built entirely on hyperscaler primitives with usage-based pricing, with data stored in an object store. This means that our fixed costs are very low, and we only need very few customers in order for this service to run profitably.

---
Created on 2026-09-08

Updated on 2026-09-08
