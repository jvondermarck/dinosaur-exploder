import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import { Press_Start_2P } from "next/font/google";
import "./globals.css";
import React from "react";
import NavBar from "@/components/NavBar";
import Footer from "@/components/Footer";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

const pressStart2P = Press_Start_2P({
  weight: "400",
  variable: "--font-press-start-2p",
  subsets: ["latin"],
  display: "swap",
});

export const metadata: Metadata = {
    title: "Dinosaur Exploder",
    description: "Open source retro shoot 'em up game.",
};

export default function RootLayout({
        children,
    }: {
    children: React.ReactNode;
}) {
    return (
        <html lang="en" className={`${geistSans.variable} ${geistMono.variable} ${pressStart2P.variable}`}>
            <body className="antialiased">
                <div className="min-h-screen flex flex-col bg-gradient-to-b from-green-100 via-white to-green-50">
                    <NavBar />
                    <main className="flex-1 flex flex-col">{children}</main>
                    <Footer />
                </div>
            </body>
        </html>
    );
}