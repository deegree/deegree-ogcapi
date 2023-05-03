| Main (1.x)                                                                                                                                                | Release                                                                                                                                                                    | License |
|-----------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|
| [![Build Status](https://buildserver.deegree.org/buildStatus/icon?job=deegree-ogcapi-BUILD)](https://buildserver.deegree.org/view/deegree-ogcapi/job/deegree-ogcapi-BUILD/) | [![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/deegree/deegree-ogcapi?sort=semver)](https://github.com/deegree/deegree-ogcapi/releases/latest) | [![License](https://img.shields.io/badge/License-LGPL%20v2.1-blue.svg)](https://www.gnu.org/licenses/lgpl-2.1) |

# deegree-ogcapi
The deegree OGC API Features (deegree ogcapi) implementation is compliant to the latest [OGC Standards called OGC API - Features](https://ogcapi.ogc.org/) based on [deegree webservices API](https://github.com/deegree/deegree3).

## User documentation
The user documentation (e.g. "How to set up deegree ogcapi?") can be found in the [user manual](./deegree-ogcapi-documentation/src/main/asciidoc/index.adoc).

## Developer documentation
To build deegree ogcapi you need to install a [JDK 11](https://adoptium.net/?variant=openjdk11&jvmVariant=hotspot) or higher and [Apache Maven 3.8.x](https://maven.apache.org/). Then run the following command to build the project:

```shell
mvn clean install
```

Further information about how to build deegree webservices can be found on the GitHub Wiki: 
https://github.com/deegree/deegree3/wiki

## Docker

### Build Docker image

You can build a Docker image easily with the `Dockerfile` contained in the repository. For example:

```
docker build -t deegree/deegree-ogcapi:latest .
```

### Run and configure Docker container

Based on a Docker image built as described above, you can use it to run containers as follows.

To start a Docker container with the name *ogcapi* on port 8080 run the following command:

```
docker run --name ogcapi -d -p 8080:8080 deegree/deegree-ogcapi:latest
```

By default the application runs in the root context, so you can then access the Datasets overview page at <http://localhost:8080/datasets>.

The container expects the configuration for the default workspace, the API key and the workspace folders at `/workspaces`.
You can mount a folder to this location or use a volume.
If you want to use a different folder within the container you need to override the environment variable `DEEGREE_WORKSPACE_ROOT`.

See the [Docker CLI documentation](https://docs.docker.com/engine/reference/commandline/cli/) for more information how to connect a container to a network, mount a volume into the container, or set environment variables.

#### Configure the context path

By default using the Docker image deegree ogcapi is served at the root context (`/`).

You can use the environment variable `DEEGREE_CONTEXT_PATH` to use a different context path.

For example, if you run the container using this command, the context path `/deegree-services-oaf` is used and the Datasets overview page is available at <http://localhost:8080/deegree-services-oaf/datasets>:

```
docker run --name ogcapi -d -p 8080:8080 -e DEEGREE_CONTEXT_PATH=deegree-services-oaf deegree/deegree-ogcapi:latest
```

To use a context path with multiple path segments, you need to use `#` as a separator.
So if you for instance configure `DEEGREE_CONTEXT_PATH` as `deegree#is#awesome` then the context path is `/deegree/is/awesome`.

#### Configure an API key

By default deegree will generate a random API key if it does not find one.

In some cases it can be helpful to set a specific API key when running a container. This can be done using the `DEEGREE_API_KEY` environment variable.

By default, or if it is set to an empty value, nothing will be done and the API key configured in the `/workspaces` folder is used, or if it is not present, it is generated by deegree ogcapi.

If you set it to a custom non-empty value though, it will ovewrite any existing API key configuration file with the configured API key.

#### Set system properties

In case you want to set specific Java system properties, for instance for configuration options in deegree ogcapi that can be set with a system property, you can specify them through the environment variable `CATALINA_OPTS`.

The `CATALINA_OPTS` variable allows to configure Java runtime options for running Tomcat.

To specify a system property add an argument like this to `CATALINA_OPTS`: `-D<name>=<value>`
In the example `<name>` is a placeholder for the name of the system property to set, while `<value>` is a placeholder for the value to set for the system property.


## License
deegree ogcapi is distributed under the [GNU Lesser General Public License, Version 2.1 (LGPL 2.1)](LICENSE).

## Contribution guidelines
First off all, thank you for taking the time to contribute to deegree ogcapi project! :+1: :tada:

By participating you are expected to uphold the [OSGeo deegree contribution guidelines](https://github.com/deegree/deegree3/blob/master/CONTRIB.md).

## Sponsors
### Initial sponsor and implementation partner 

<p align="center">
  <a href="https://geoinfo.hamburg.de/" target="_blank">
    <img width="260px" src="sponsor_lgv.png">
  </a>
</p>

### Supporting partners

<p align="center">
  <a href="https://www.geobasis-bb.de/" target="_blank">
    <img width="130px" src="sponsor_lgb.jpg">
  </a>
</p>

### Sponsoring 
Use [GitHub Sponsors](https://github.com/sponsors/OSGeo) to donate via GitHub or you can submit your donation via [PayPal](https://www.paypal.com/donate/?cmd=_s-xclick&hosted_button_id=NWV8QNKA36YGL&source=url).

The OSGeo Treasurer will contact you to acknowledge your sponsorship. Please have a logo ready for your organization if you wish to be recognized publicly.
