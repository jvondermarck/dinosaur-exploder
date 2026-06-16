import fs from 'node:fs'
import path from 'node:path'
import { MetadataRoute } from 'next'
import { i18n } from '../i18n-config'

/**
 * Recursively discovers all public routes by scanning the app/[lang]/ directory
 * for `page.tsx` (or `.ts`, `.js`, `.jsx`) files.
 *
 * Edge cases handled:
 * - Dynamic segments like `[id]` or `[slug]` are skipped (not indexable without params)
 * - Route groups like `(marketing)` are transparent — their children are included
 * - Special Next.js files (layout, loading, error, etc.) are ignored; only `page.*` matters
 * - Nested directories without a page file are naturally excluded
 */
function getRoutes(): string[] {
  const langDir = path.join(process.cwd(), 'app', '[lang]')

  if (!fs.existsSync(langDir)) {
    return ['']
  }

  const routes: string[] = []

  function scan(dir: string, routePrefix: string): void {
    const entries = fs.readdirSync(dir, { withFileTypes: true })

    // Check if this directory itself contains a page file
    const hasPage = entries.some(
      (e) => e.isFile() && /^page\.(tsx|ts|jsx|js)$/.test(e.name)
    )
    if (hasPage) {
      routes.push(routePrefix || '')
    }

    // Recurse into subdirectories
    for (const entry of entries) {
      if (!entry.isDirectory()) continue

      const name = entry.name

      // Skip dynamic route segments — they require runtime params and are not indexable
      if (name.startsWith('[') && name.endsWith(']')) continue

      // Skip private folders (Next.js convention: underscore prefix)
      if (name.startsWith('_')) continue

      // Route groups like (group) are transparent — don't add to the URL path
      if (name.startsWith('(') && name.endsWith(')')) {
        scan(path.join(dir, name), routePrefix)
        continue
      }

      scan(path.join(dir, name), `${routePrefix}/${name}`)
    }
  }

  scan(langDir, '')
  return routes
}

/**
 * Generates a dynamic sitemap for the application.
 * Routes are auto-discovered from the filesystem — no manual updates needed when adding pages.
 * Supports multiple locales and includes alternate language references (hreflang) for SEO.
 */
export default function sitemap(): MetadataRoute.Sitemap {
  const baseUrl = process.env.NEXT_PUBLIC_SITE_URL || 'https://dinosaur-exploder.vercel.app'
  const routes = getRoutes()

  return i18n.locales.flatMap((locale) =>
    routes.map((route) => {
      const url = `${baseUrl}/${locale}${route}`

      return {
        url,
        lastModified: new Date(),
        changeFrequency: 'monthly',
        priority: route === '' ? 1 : 0.8,
        alternates: {
          languages: Object.fromEntries(
            i18n.locales.map((lang) => [
              lang.replace('_', '-'), // Normalize locale for hreflang (e.g., zh_cn -> zh-cn)
              `${baseUrl}/${lang}${route}`,
            ])
          ),
        },
      }
    })
  )
}
