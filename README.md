# SimpleSpringWebMVC

Use `mvn spring-boot:run` to bootstrap your application.

To describe what `spring-boot:run` command supports, use:

`mvn help:describe -Dcmd=spring-boot:run -Ddetail`

With spring-boot:run, one should pass arguments using `-Dspring-boot.run.arguments` and don't forget the spring parameters names are `--` prefixed, like the example below:

`mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"`

