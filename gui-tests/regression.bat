call mvn clean 
call mvn test -Dselenide.browser=chrome -Dthread.count=2
cd target
call allure serve