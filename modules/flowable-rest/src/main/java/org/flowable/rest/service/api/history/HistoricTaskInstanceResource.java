/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flowable.rest.service.api.history;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.flowable.engine.HistoryService;
import org.flowable.engine.common.api.FlowableObjectNotFoundException;
import org.flowable.engine.history.HistoricTaskInstance;
import org.flowable.rest.service.api.RestResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Tijs Rademakers
 */
@RestController
@Api(tags = { "History" }, description = "Manage History", authorizations = { @Authorization(value = "basicAuth") })
public class HistoricTaskInstanceResource {

    @Autowired
    protected RestResponseFactory restResponseFactory;

    @Autowired
    protected HistoryService historyService;

    @ApiOperation(value = "Get a single historic task instance", tags = { "History" }, notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates that the historic task instances could be found."),
            @ApiResponse(code = 404, message = "Indicates that the historic task instances could not be found.") })
    @RequestMapping(value = "/history/historic-task-instances/{taskId}", method = RequestMethod.GET, produces = "application/json")
    public HistoricTaskInstanceResponse getTaskInstance(@ApiParam(name = "taskId") @PathVariable String taskId, HttpServletRequest request) {
        return restResponseFactory.createHistoricTaskInstanceResponse(getHistoricTaskInstanceFromRequest(taskId));
    }

    @ApiOperation(value = "Delete a historic task instance", tags = { "History" }, notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Indicates that the historic task instance was deleted."),
            @ApiResponse(code = 404, message = "Indicates that the historic task instance could not be found.") })
    @RequestMapping(value = "/history/historic-task-instances/{taskId}", method = RequestMethod.DELETE)
    public void deleteTaskInstance(@ApiParam(name = "taskId") @PathVariable String taskId, HttpServletResponse response) {
        historyService.deleteHistoricTaskInstance(taskId);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    protected HistoricTaskInstance getHistoricTaskInstanceFromRequest(String taskId) {
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (taskInstance == null) {
            throw new FlowableObjectNotFoundException("Could not find a task instance with id '" + taskId + "'.", HistoricTaskInstance.class);
        }
        return taskInstance;
    }
}
