import { MetadataRoute } from 'next'
import { i18n } from '../i18n-config'

/**
 * Generates a dynamic sitemap for the application.
 * Supports multiple locales and includes alternate language references (hreflang) for SEO.
 */
export default function sitemap(): MetadataRoute.Sitemap {
  const baseUrl = process.env.NEXT_PUBLIC_SITE_URL || 'https://dinosaur-exploder.vercel.app'
  const routes = ['', '/contact', '/credits', '/how-game-works']

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
              `${baseUrl}/${lang}${route}`
            ])
          ),
        },
      }
    })
  )
}
