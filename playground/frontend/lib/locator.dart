/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import 'package:app_state/app_state.dart';
import 'package:get_it/get_it.dart';
import 'package:playground_components/playground_components.dart';

import 'config.g.dart';
import 'pages/loading/page.dart';
import 'router/page_factory.dart';
import 'router/route_information_parser.dart';

Future<void> initializeServiceLocator() async {
  _initializeRepositories();
  _initializeRouter();
}

void _initializeRepositories() {
  GetIt.instance.registerSingleton(CodeRepository(
    client: GrpcCodeClient(
      url: kApiClientURL,
      runnerUrlsById: {
        Sdk.java.id: kApiJavaClientURL,
        Sdk.go.id: kApiGoClientURL,
        Sdk.python.id: kApiPythonClientURL,
        Sdk.scio.id: kApiScioClientURL,
      },
    ),
  ));

  GetIt.instance.registerSingleton(ExampleRepository(
    client: GrpcExampleClient(url: kApiClientURL),
  ));
}

void _initializeRouter() {
  GetIt.instance.registerSingleton(
    PageStack(
      bottomPage: LoadingPage(),
      createPage: PageFactory.createPage,
      routeInformationParser: PlaygroundRouteInformationParser(),
    ),
  );

  GetIt.instance.registerSingleton<PageStackRouteInformationParser>(
    PlaygroundRouteInformationParser(),
  );
  print('Initialized PageStackRouteInformationParser');
}
