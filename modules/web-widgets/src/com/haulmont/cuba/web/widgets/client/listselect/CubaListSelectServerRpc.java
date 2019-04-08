/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.cuba.web.widgets.client.listselect;

import com.vaadin.shared.communication.ServerRpc;

/**
 * RPC interface for calls from client to server.
 */
public interface CubaListSelectServerRpc extends ServerRpc {
    /**
     * Indicates to the server that the client has double-clicked
     * the list item.
     *
     * @param itemIndex index of an item in the list
     */
    void onDoubleClick(Integer itemIndex);
}
