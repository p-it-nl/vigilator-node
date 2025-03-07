/**
 * Copyright (c) p-it
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
*** Settings ***
Documentation    Test suite for Vigilator node
Resource         variables.robot
Library          RequestsLibrary
Library          Collections
Library          String

Suite Setup    Create Session    VigilatorNode    ${uri}

*** Test Cases ***
Clear Before - All tests
    Clean

Retrieve status update from Vigilator node 
    [Documentation]    Retrieving status update from node
    [Tags]             Expecting status
    ${resp}=           GET On Session                         VigilatorNode                       /status      

    Log To Console    ${resp[1]}

Clear After - All tests
    Clean

*** Keywords ***
Clean
    Log To Console    clearing
