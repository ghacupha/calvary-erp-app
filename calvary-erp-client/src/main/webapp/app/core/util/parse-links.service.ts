///
/// Erp System - Mark I No 2 (Beniah Series) Client 0.0.1-SNAPSHOT
/// Copyright © 2022 - 2025 Edwin Njeru (mailnjeru@gmail.com)
///
/// This program is free software: you can redistribute it and/or modify
/// it under the terms of the GNU General Public License as published by
/// the Free Software Foundation, either version 3 of the License, or
/// (at your option) any later version.
///
/// This program is distributed in the hope that it will be useful,
/// but WITHOUT ANY WARRANTY; without even the implied warranty of
/// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
/// GNU General Public License for more details.
///
/// You should have received a copy of the GNU General Public License
/// along with this program. If not, see <http://www.gnu.org/licenses/>.
///

import { Injectable } from '@angular/core';

/**
 * An utility service for link parsing.
 */
@Injectable({
  providedIn: 'root',
})
export class ParseLinks {
  /**
   * Method to parse the links
   */
  parseAll(header: string): Record<string, Record<string, string | undefined> | undefined> {
    if (header.length === 0) {
      throw new Error('input must not be of zero length');
    }

    // Split parts by comma
    const parts: string[] = header.split(',');

    // Parse each part into a named link
    return Object.fromEntries(
      parts.map(p => {
        const section: string[] = p.split(';');

        if (section.length !== 2) {
          throw new Error('section could not be split on ";"');
        }

        const url: string = section[0].replace(/<(.*)>/, '$1').trim(); // NOSONAR
        const queryString: Record<string, string> = {};

        url.replace(/([^?=&]+)(=([^&]*))?/g, (_$0: string, $1: string | undefined, _$2: string | undefined, $3: string | undefined) => {
          if ($1 !== undefined && $3 !== undefined) {
            queryString[$1] = decodeURIComponent($3);
          }
          return $3 ?? '';
        });

        const name: string = section[1].replace(/rel="(.*)"/, '$1').trim();
        return [name, queryString];
      }),
    );
  }

  /**
   * Method to parse the links
   */
  parse(header: string): Record<string, number> {
    const sections = this.parseAll(header);
    const links: Record<string, number> = {};
    for (const [name, queryParams] of Object.entries(sections)) {
      if (queryParams?.page !== undefined) {
        links[name] = parseInt(queryParams.page, 10);
      }
    }
    return links;
  }
}
