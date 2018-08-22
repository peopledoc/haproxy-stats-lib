HAProxy monitoring library
==========================

Overview
--------

Java library to poll statistics from HAProxy instances and give back a
formatted JSON file to use in another applications.


Key Features
------------
- Configurable with a YAML file.
- Give back a JSON with some data in it.

Maven usage
-----------

```xml
<dependency>
    <groupId>com.peopledoc</groupId>
    <artifactId>haproxy-stats</artifactId>
    <version>1.2.0</version>
</dependency>
```

Classes to use
--------------

* `com.peopledoc.haproxystats.HAProxyChecker` to check HAProxy stats.
* `com.peopledoc.statsretriever.StatsRetriever` to collect results from multiple servers providing results 
  from `HAProxyChecker`.

Config file format (verbose)
----------------------------

For `com.peopledoc.haproxystats.HAProxyChecker` :

```yaml
environment: env-name

# HAProxy instances list
loadbalancers:
  - name: haproxy1
    url: http://haproxy1.example.com
    username: userA
    password: pass

# optional whitelist of proxies
proxies:
  - app1
  - app2
```

For `com.peopledoc.statsretriever.StatsRetriever` :

```yaml
integration: https://integration.example.com
test: https://test.example.com
```

Result sample
-------------

```json
{
  "test" : [ {
    "name" : "app1",
    "backends" : [ {
      "name" : "app1-A",
      "weight" : 0,
      "available" : true,
      "status" : "DISABLED"
    }, {
      "name" : "app1-B",
      "weight" : 100,
      "available" : true,
      "status" : "OK"
    } ],
    "loadbalancer" : "haproxy1"
  } ]
}
```

<!--
Team
----
[Team](https://github.com/peopledoc/tribe-java/blob/master/documentation/applications.md)


Contributing
------------
[Contributing](https://github.com/peopledoc/tribe-java/blob/master/documentation/contribution.md)
-->

License
-------

Apache License v2.0 - Copyright 2018 Eliah Rebstock

