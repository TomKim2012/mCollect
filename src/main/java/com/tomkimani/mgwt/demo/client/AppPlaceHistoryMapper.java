
/*
 * Copyright 2010 Daniel Kurka
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tomkimani.mgwt.demo.client;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.tomkimani.mgwt.demo.client.places.ContactPlace.ContactPlaceTokenizer;
import com.tomkimani.mgwt.demo.client.places.CustomerSearchPlace.CustomerSearchPlaceTokenizer;
import com.tomkimani.mgwt.demo.client.places.DashboardPlace.DashboardPlaceTokenizer;
import com.tomkimani.mgwt.demo.client.places.LoginPlace.LoginPlaceTokenizer;
import com.tomkimani.mgwt.demo.client.places.SearchResultsPlace.SearchResultsPlaceTokenizer;
import com.tomkimani.mgwt.demo.client.places.SettingsPlace.SettingsPlaceTokenizer;
import com.tomkimani.mgwt.demo.client.places.TransactionDetailPlace.TransactionDetailPlaceTokenizer;
import com.tomkimani.mgwt.demo.client.places.TransactionsPlace.TransactionsPlaceTokenizer;

/**
 * @author Tom Kimani
 * 
 */
@WithTokenizers({DashboardPlaceTokenizer.class,LoginPlaceTokenizer.class,TransactionsPlaceTokenizer.class,
				TransactionDetailPlaceTokenizer.class,CustomerSearchPlaceTokenizer.class, SearchResultsPlaceTokenizer.class,
				SettingsPlaceTokenizer.class,ContactPlaceTokenizer.class
			   })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
