The JeeTS Integration Tests
===========================

The JeeTS Source Repository is hosting the code for Java EE Tracking Components.  
The Name JeeTS reflects "GPS Tracking System with Java EE", i.e. "Jee" T.S. for G.T.S.   
More details can (soon) be found on http://jeets.org

The itests, i.e. JeeTS integration tests directory, can be run to test various
JeeTS components against each other, like tracker2dcs. This directory should 
not include Unit Testing, which can be found in each individual component project.
Therfore the standard test folders are used for integration tests.
Vice versa the components do not include integration testing (yet!?).


## Usage

All tests, i.e. Unit and Integration tests can be launched for the complete repo  
Without the itests profile only JUnit tests are executed:

    ...\github.jeets>
	mvn test   -> surefire Unit for all aggregates (models, clients, ... )
	mvn verify -> surefire Unit tests just like mvn test
	
With the itests profile only Integration tests are executed:
	
	mvn test   -Pitests	 -> skips all tests
	mvn verify -Pitests	 -> integration tests for itests, skips Unit tests

In order to keep it this way  
	all aggregates - except itests - should only implement Unit tests  
	only itests should implement integration tests to test JeeTS components against each other
	and should not implement Unit tests


## License

    Apache License, Version 2.0

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
