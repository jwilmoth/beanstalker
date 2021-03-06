/*
 * Copyright (c) 2016 ingenieux Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.ingenieux.mojo.lambda;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.lang.builder.CompareToBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LambdaFunctionDefinition implements Serializable, Comparable<LambdaFunctionDefinition> {
  String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  String alias = "";

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  String description = "";

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  int memorySize;

  public int getMemorySize() {
    return memorySize;
  }

  public void setMemorySize(int memorySize) {
    this.memorySize = memorySize;
  }

  String role;

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  int timeout;

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  String handler;

  public String getHandler() {
    return handler;
  }

  public void setHandler(String handler) {
    this.handler = handler;
  }

  @Override
  public int compareTo(LambdaFunctionDefinition o) {
    if (null == o) return -1;

    if (this == o) return 0;

    return new CompareToBuilder().append(this.name, o.name).toComparison();
  }

  ObjectNode api;

  public ObjectNode getApi() {
    return api;
  }

  public void setApi(ObjectNode api) {
    this.api = api;
  }

  List<String> bindings = new ArrayList<>();

  public List<String> getBindings() {
    return bindings;
  }

  public void setBindings(List<String> bindings) {
    this.bindings = bindings;
  }

  String arn;

  public String getArn() {
    return arn;
  }

  public void setArn(String arn) {
    this.arn = arn;
  }

  String version;

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
