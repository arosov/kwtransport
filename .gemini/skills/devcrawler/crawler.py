import asyncio
import sys
import argparse
import os
from crawl4ai import AsyncWebCrawler, CrawlerRunConfig
from crawl4ai.deep_crawling import BFSDeepCrawlStrategy

# Suppress crawl4ai logs if they go to stdout
os.environ["CRAWL4AI_LOG_LEVEL"] = "ERROR"

async def crawl(url, max_pages, max_depth, output_format):
    # Mapping output format to markdown generator parameters if needed
    # But for now we just use the default markdown output from results
    
    config = CrawlerRunConfig(
        deep_crawl_strategy=BFSDeepCrawlStrategy(max_depth=max_depth, max_pages=max_pages),
        verbose=False
    )
    
    async with AsyncWebCrawler() as crawler:
        results = await crawler.arun(
            url=url,
            config=config
        )
        
        if not results:
            print(f"No results found for {url}", file=sys.stderr)
            return

        if not isinstance(results, list):
            results = [results]
            
        full_markdown = []
        for i, res in enumerate(results):
            if res.success:
                full_markdown.append(f"<!-- SOURCE: {res.url} -->\n")
                # Choose markdown based on format
                if output_format == "md-fit":
                    # fit_markdown might not be available in all versions, 
                    # let's check what's available
                    content = getattr(res, 'fit_markdown', res.markdown)
                else:
                    content = res.markdown
                full_markdown.append(content)
                full_markdown.append("\n\n---\n\n")
            else:
                print(f"Failed to crawl {res.url}: {res.error_message}", file=sys.stderr)
                
        if full_markdown:
            print("".join(full_markdown))
        else:
            print("No content successfully crawled.", file=sys.stderr)

def main():
    parser = argparse.ArgumentParser(description="Crawl4AI Wrapper for DevCrawler")
    parser.add_argument("url", help="URL to crawl")
    parser.add_argument("--max-pages", type=int, default=1, help="Max pages to crawl")
    parser.add_argument("--max-depth", type=int, default=2, help="Max depth for deep crawl")
    parser.add_argument("--format", choices=["markdown", "md-fit"], default="md-fit", help="Output format")
    
    args = parser.parse_args()
    
    asyncio.run(crawl(args.url, args.max_pages, args.max_depth, args.format))

if __name__ == "__main__":
    main()
