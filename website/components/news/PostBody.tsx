/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

type Props = { contentHtml: string };

export default function PostBody({ contentHtml }: Props) {
  return (
    <div
      className="post-body"
      dangerouslySetInnerHTML={{ __html: contentHtml }}
    />
  );
}
