@echo off
chcp 65001 > nul
setlocal

set BASE_URL=https://flashnote.rejs.link
set SCRIPT_FILE=note_search_prod.js
set OUTPUT_DIR=.\test-results
set TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%

k6 run -e BASE_URL=%BASE_URL% --summary-export=%OUTPUT_DIR%\summary_%TIMESTAMP%.json %SCRIPT_FILE%

pause