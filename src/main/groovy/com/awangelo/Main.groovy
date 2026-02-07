package com.awangelo

import com.awangelo.service.CrawlerService
import com.awangelo.service.DownloadService

static void main(String[] args) {
    CrawlerService crawler = new CrawlerService()
    DownloadService downloader = new DownloadService()

    println 'Iniciando busca'
    def documentos = crawler.buscarDocumentosTiss()
    println documentos
}