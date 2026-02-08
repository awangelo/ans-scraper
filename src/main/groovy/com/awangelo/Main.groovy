package com.awangelo

import com.awangelo.service.CrawlerService
import com.awangelo.service.DownloadService

static void main(String[] args) {
    CrawlerService crawler = new CrawlerService()
    DownloadService downloader = new DownloadService()

    println 'Preparando diretório de saída...'
    DownloadService.prepararDiretorio()

    Map<String, Object> resultados = crawler.buscarDocumentosTiss()

    println ' -> Gerando CSV do Histórico de Versões...'
    DownloadService.salvarCsvHistorico(resultados.tabelaHistorico as List)

    println '-> Baixando Componente de Comunicação...'
    DownloadService.baixarStream(resultados.urlDownload as String, 'componente_comunicacao.zip')

    println ' -> Baixando Tabela de Erros...'
    DownloadService.baixarStream(resultados.urlTabelaErros as String, 'tabela_erros.xlsx')
}