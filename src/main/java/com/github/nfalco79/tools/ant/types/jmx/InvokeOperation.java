/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.nfalco79.tools.ant.types.jmx;

import java.util.ArrayList;
import java.util.Collection;

public class InvokeOperation extends AbstractMBeanType {

	public static class Parameter {
		private String value;
		private String mBeanRefId;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getmBeanRefId() {
			return mBeanRefId;
		}

		public void setmBeanRefId(String mBeanRefId) {
			this.mBeanRefId = mBeanRefId;
		}

	}

	private String operation;
	private Collection<Parameter> parameters = new ArrayList<Parameter>();

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Collection<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(Collection<Parameter> parmeters) {
		this.parameters = parmeters;
	}

	public void addParameter(Parameter parmeter) {
		parameters.add(parmeter);
	}

}