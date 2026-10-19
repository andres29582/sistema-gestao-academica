#Requires -Version 7.0
param([string]$JavaHome = $env:JAVA_HOME, [int]$Porta = 18080)
$ErrorActionPreference = 'Stop'
$backend = Split-Path $PSScriptRoot -Parent
$projeto = Split-Path $backend -Parent
if ([string]::IsNullOrWhiteSpace($JavaHome)) {
    $JavaHome = Join-Path $projeto '.ferramentas\java\jdk-21.0.12.1+1'
}
$java = Join-Path $JavaHome 'bin\java.exe'
if (-not (Test-Path -LiteralPath $java)) { throw 'Informe JavaHome com um JDK 21.' }
$jar = Join-Path $backend 'target\gestao-academica-1.0.0-beta.1.jar'
if (-not (Test-Path -LiteralPath $jar)) { throw 'Compile primeiro com Maven Wrapper: verify.' }
$saida = Join-Path $backend 'target\execucao'
New-Item -ItemType Directory -Force -Path $saida | Out-Null
$base = "http://127.0.0.1:$Porta"
$resultados = @()
for ($rodada = 1; $rodada -le 2; $rodada++) {
    $processo = Start-Process -FilePath $java -ArgumentList @('-jar', ('"' + $jar + '"'), "--server.port=$Porta", '--server.address=127.0.0.1', '--app.demo=true') -WorkingDirectory $backend -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $saida "aplicacao-$rodada.log") -RedirectStandardError (Join-Path $saida "erro-$rodada.log")
    try {
        $limite = (Get-Date).AddMinutes(5)
        $pronto = $false
        do {
            if ($processo.HasExited) { throw "Aplicação encerrou antes de responder. Consulte target/execucao/erro-$rodada.log." }
            try { $catalogo = Invoke-RestMethod "$base/api/cursos" -TimeoutSec 3; $pronto = $true }
            catch { Start-Sleep -Seconds 1 }
        } until ($pronto -or (Get-Date) -gt $limite)
        if (-not $pronto) { throw 'Aplicação não respondeu no prazo de cinco minutos.' }
        # Tomcat aceita HTTP antes de o ApplicationRunner confirmar a transação da carga.
        $limiteDados = (Get-Date).AddSeconds(180)
        $demoPronta = $false
        do {
            try {
                $catalogo = Invoke-RestMethod "$base/api/cursos" -TimeoutSec 5
                if ($catalogo.Count -eq 3) {
                    $respostaLogin = Invoke-WebRequest "$base/api/auth/login" -Method Post -ContentType 'application/json' -Body '{"email":"admin@demo.local","senha":"Demo@12345"}' -TimeoutSec 10
                    if ($respostaLogin.StatusCode -eq 200) {
                        $login = $respostaLogin.Content | ConvertFrom-Json
                        $demoPronta = -not [string]::IsNullOrWhiteSpace($login.token)
                    }
                }
            } catch { }
            if (-not $demoPronta) { Start-Sleep -Seconds 1 }
        } until ($demoPronta -or (Get-Date) -gt $limiteDados)
        if (-not $demoPronta) { throw 'Carga demonstrativa e login não ficaram prontos em180s após o primeiroHTTP.' }
        $cabecalho = @{Authorization = "Bearer $($login.token)"}
        $dashboard = Invoke-RestMethod "$base/api/admin/dashboard" -Headers $cabecalho
        if ($dashboard.alunosAtivos -ne 1 -or $dashboard.matriculasAtivas -ne 2) { throw 'Indicadores iniciais divergentes.' }
        $openapi = Invoke-RestMethod "$base/v3/api-docs"
        $swagger = Invoke-WebRequest "$base/swagger-ui/index.html"
        if ($swagger.StatusCode -ne 200 -or -not $openapi.components.securitySchemes.bearerAuth) { throw 'Swagger/OpenAPI indisponível.' }
        $invalidos = Invoke-RestMethod "$base/api/auth/register" -Method Post -ContentType 'application/json' -Body '{}' -SkipHttpErrorCheck -StatusCodeVariable statusValidacao
        [pscustomobject]@{status=[int]$statusValidacao;codigo=$invalidos.codigo;detail=$invalidos.detail;campos=$invalidos.campos} | ConvertTo-Json -Depth 5 | Set-Content -Encoding utf8 -LiteralPath (Join-Path $saida "validacao-$rodada.json")
        if ([int]$statusValidacao -ne 400 -or $invalidos.codigo -ne 'DADOS_INVALIDOS') { throw 'Contrato de validação divergente. Consulte target/execucao/validacao-*.json (sem credenciais).' }
        if ($rodada -eq 1) {
            $corpo = @{nome='Aluno Efêmero';email='efemero@demo.local';telefone='11999999999';senha='Demo@12345';cursoId=$catalogo[0].id} | ConvertTo-Json
            $resposta = Invoke-WebRequest "$base/api/auth/register" -Method Post -ContentType 'application/json; charset=utf-8' -Body ([Text.Encoding]::UTF8.GetBytes($corpo))
            if ($resposta.StatusCode -ne 201) { throw 'Cadastro real não concluiu com 201.' }
        } else {
            $lista = Invoke-RestMethod "$base/api/alunos" -Headers $cabecalho
            if ($lista | Where-Object email -eq 'efemero@demo.local') { throw 'Dados efêmeros sobreviveram ao reinício.' }
            if ($lista.Count -ne 5) { throw 'Demonstração não foi restaurada após reinício.' }
        }
        $resultados += [pscustomobject]@{rodada=$rodada;iniciou=$true;cursos=$catalogo.Count;swaggerHttp=$swagger.StatusCode;alunosAtivos=$dashboard.alunosAtivos;matriculasAtivas=$dashboard.matriculasAtivas;reinicioConferido=($rodada -eq 2)}
    } finally {
        if (-not $processo.HasExited) { $processo.Kill(); $processo.WaitForExit(15000) | Out-Null }
    }
}
$relatorio = [pscustomobject]@{executadoEm=[DateTime]::UtcNow.ToString('o');java=$java;rodadas=$resultados;resultado='APROVADO'}
$relatorio | ConvertTo-Json -Depth 5 | Set-Content -Encoding utf8 -LiteralPath (Join-Path $saida 'resultado.json')
$relatorio | ConvertTo-Json -Depth 5
